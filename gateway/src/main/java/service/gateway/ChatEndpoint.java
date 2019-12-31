package service.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import message.ChatLogRequest;
import message.MessageContainer;
import message.SessionMessage;
import message.UserMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatEndpoint extends WebSocketServer {

    private Map<String, WebSocket> cache;

    public ChatEndpoint(InetSocketAddress address) {
        super(address);
        cache = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
        Gson builder = new GsonBuilder().setPrettyPrinting().create();
        conn.send(builder.toJson(getSessionsList()));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        RuntimeTypeAdapterFactory<message.Message> adapter = RuntimeTypeAdapterFactory
                .of(message.Message.class, "type")
                .registerSubtype(SessionMessage.class, MessageContainer.MessageTypes.SESSION_MESSAGE)
                .registerSubtype(UserMessage.class, MessageContainer.MessageTypes.USER_MESSAGE)
                .registerSubtype(ChatLogRequest.class, MessageContainer.MessageTypes.CHAT_LOG_REQUEST);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        System.out.println("received message from " + conn.getRemoteSocketAddress() + ": " + message);

        message.Message messageObj = gson.fromJson(message, message.Message.class);

        switch (messageObj.getType()) {
            case MessageContainer.MessageTypes.USER_MESSAGE:
                UserMessage userMessage = (UserMessage) messageObj;
                processUserMessage(userMessage);
                break;
            case MessageContainer.MessageTypes.SESSION_MESSAGE:
                SessionMessage sessionMessage = (SessionMessage) messageObj;
                sendSessionMessage(sessionMessage);
                addUserToCache(sessionMessage, conn);
                break;
            case MessageContainer.MessageTypes.CHAT_LOG_REQUEST:
                List<UserMessage> messages = getChatLog((ChatLogRequest) messageObj);
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                conn.send(builder.toJson(messages));
                break;
        }

        try {
            Thread.sleep(3000);

            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            conn.send(builder.toJson(getSessionsList()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processUserMessage(UserMessage userMessage) {
        SessionMessage sessionMessage = null;

        try {
            sessionMessage = new SessionMessage(userMessage.getTimestamp(), userMessage.getSentBy(), InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (userMessage.isProcessed()) {
            WebSocket connection = cache.get(userMessage.getSentTo());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonStr = gson.toJson(userMessage);
            connection.send(jsonStr);
        } else {
            sendSessionMessage(sessionMessage);
            sendUserMessageToProcessingQueue(userMessage);
        }

        // todo: send the user message to message-service

    }

    private void addUserToCache(SessionMessage sessionMessage, WebSocket socket) {
        if (cache.containsKey(sessionMessage.getUsername())) {
            cache.put(sessionMessage.getUsername(), socket);
        }
    }

    private void sendUserMessageToProcessingQueue(UserMessage userMessage) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("gateway");
            Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            Queue requestsQueue = session.createQueue("MESSAGES");
            MessageProducer producer = session.createProducer(requestsQueue);
            producer.send(session.createObjectMessage(userMessage));

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void sendSessionMessage(SessionMessage sessionMessage) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("gateway");
            Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            Queue requestsQueue = session.createQueue("SESSIONS");
            MessageProducer producer = session.createProducer(requestsQueue);
            producer.send(session.createObjectMessage(sessionMessage));

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private List<UserMessage> getChatLog(ChatLogRequest chatLogRequest) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<UserMessage>> rateResponse =
                restTemplate.exchange("http://message-service:8080/history?sendTo="+chatLogRequest.getRequestingUser()+"&sendFrom="+chatLogRequest.getRequestedUser(),//todo make this address better
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<UserMessage>>() {
                        });

        System.out.println("Code: " + rateResponse.getStatusCodeValue());
        List<UserMessage> response = rateResponse.getBody();
        for (UserMessage message2 : response) {
            System.out.println("Content: " + message2.getTimestamp() + " - " + message2.getMessage() + " - "
                    + message2.getSentBy() + " - " + message2.getSentTo());
        }

        return response;
    }

    private List<SessionMessage> getSessionsList() {
        System.out.println("rest time...");
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<SessionMessage>> rateResponse =
                restTemplate.exchange("http://session:8080/sessions",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<SessionMessage>>() {
                        });

        System.out.println("Code: " + rateResponse.getStatusCodeValue());
        List<SessionMessage> response = rateResponse.getBody();
        System.out.println("There are " + response.size() + " messages. Neat.");
        for (SessionMessage message2 : response) {
            System.out.println("Content: " + message2.getTimestamp() + " - " + message2.getUsername() + " - " + message2.getGateway());
        }

        return response;
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

    @Override
    public void onStart() {
        connectToLoadBalancer();
        System.out.println("server started successfully");
    }

    private void connectToLoadBalancer() {
        RestTemplate restTemplate = new RestTemplate();
        String gatewayAddress = getAddress().getHostString() + ":" + getAddress().getPort();
        HttpEntity<String> request = new HttpEntity<>(gatewayAddress);
        try {
            Thread.sleep(90000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restTemplate.postForObject("http://load-balancer:8081/addGateway", request, String.class);

        System.out.println("I've posted! go me!");
    }

    public static void main(String[] args) throws UnknownHostException {
        String host = Inet4Address.getLocalHost().getHostAddress();
        int port = 8080;

        System.out.println("waiting");
        WebSocketServer server = new ChatEndpoint(new InetSocketAddress(host, port));
        server.run();
    }
}