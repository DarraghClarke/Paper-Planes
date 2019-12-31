package service.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import message.Message;
import message.*;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatEndpoint – WebSocketServer class that processes all messages sent to it by clients and any other WebSocket-based module
 */
public class ChatEndpoint extends WebSocketServer {
    // "cache" is used to store the WebSocket associated with each username. Used for sending messages from gateway to client
    private Map<String, WebSocket> cache;

    public ChatEndpoint(InetSocketAddress address) {
        super(address);
        cache = new HashMap<>();
    }

    @Override
    public void onStart() {
        connectToLoadBalancer();
        System.out.println("Server started successfully – clients can now connect");
    }

    /**
     * Adds the current gateway to the load-balancer, so it can be accessed by clients
     */
    private void connectToLoadBalancer() {
        RestTemplate restTemplate = new RestTemplate();
        String gatewayAddress = getAddress().getHostString() + ":" + getAddress().getPort();
        HttpEntity<String> request = new HttpEntity<>(gatewayAddress);

        // Since we are using Docker Compose, we need to wait for the load-balancer to come online. This might need tuning
        // on your individual system.
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Send the current gateway address to the load-balancer
        restTemplate.postForObject("http://load-balancer:8081/gateways", request, String.class);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Send the user a list of all users in the system on connection start
        List<SessionMessage> sessionMessageList = getSessionsList();
        Gson builder = new GsonBuilder().setPrettyPrinting().create();
        conn.send(builder.toJson(new ListOfSessionMessages(sessionMessageList)));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // This makes the decoding of JSON passed from WebSocketClient objects much more elegantly
        RuntimeTypeAdapterFactory<message.Message> adapter = RuntimeTypeAdapterFactory
                .of(message.Message.class, "type")
                .registerSubtype(SessionMessage.class, Message.MessageTypes.SESSION_MESSAGE)
                .registerSubtype(ChatMessage.class, Message.MessageTypes.USER_MESSAGE)
                .registerSubtype(ChatLogRequest.class, Message.MessageTypes.CHAT_LOG_REQUEST)
                .registerSubtype(ListOfChatMessages.class, Message.MessageTypes.LIST_OF_USER_MESSAGES)
                .registerSubtype(ListOfSessionMessages.class, Message.MessageTypes.LIST_OF_SESSION_MESSAGES);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        message.Message messageObj = gson.fromJson(message, message.Message.class);

        // We check the type of the message received by the server, and depending on the type, process it differently
        switch (messageObj.getType()) {
            case Message.MessageTypes.USER_MESSAGE:
                // Since we know the type, we can cast safely
                ChatMessage chatMessage = (ChatMessage) messageObj;
                processChatMessage(chatMessage);
                break;
            case Message.MessageTypes.SESSION_MESSAGE:
                SessionMessage sessionMessage = (SessionMessage) messageObj;
                sendSessionMessage(sessionMessage);
                addUserToCache(sessionMessage, conn);
                break;
            case Message.MessageTypes.CHAT_LOG_REQUEST:
                List<ChatMessage> messages = getChatLog((ChatLogRequest) messageObj);
                ListOfChatMessages listOfChatMessages = new ListOfChatMessages(messages, (ChatLogRequest) messageObj);
                Gson builder = new GsonBuilder().setPrettyPrinting().create();
                conn.send(builder.toJson(listOfChatMessages));
                break;
        }

        // We sleep for a bit to make sure the previous commands have time to be processed by the system
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Any time the client sends a message, we want to return an up-to-date list of users
        Gson builder = new GsonBuilder().setPrettyPrinting().create();
        List<SessionMessage> sessionMessageList = getSessionsList();
        conn.send(builder.toJson(new ListOfSessionMessages(sessionMessageList)));
    }

    /**
     * Method that processes a chat message through the system
     *
     * @param chatMessage the message to process
     */
    private void processChatMessage(ChatMessage chatMessage) {
        SessionMessage sessionMessage = null;

        // We create the SessionMessage with the gateway's host
        try {
            sessionMessage = new SessionMessage(chatMessage.getTimestamp(), chatMessage.getSentBy(), InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // If the message has already been processed by the system, we want to send the ChatMessage to a client
        if (chatMessage.isProcessed()) {
            // We lookup the cache to get the WebSocket of the receiver of the message and send the message
            WebSocket connection = cache.get(chatMessage.getSentTo());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonStr = gson.toJson(chatMessage);
            connection.send(jsonStr);
        } else {
            // If the message hasn't been processed yet, we send a SessionMessage to sessions and forward the chatMessage to a queue
            sendSessionMessage(sessionMessage);
            sendChatMessageToProcessingQueue(chatMessage);
        }
    }

    /**
     * Adds a user's web socket to the cache, if not already included
     */
    private void addUserToCache(SessionMessage sessionMessage, WebSocket socket) {
        if (!cache.containsKey(sessionMessage.getUsername())) {
            cache.put(sessionMessage.getUsername(), socket);
        }
    }

    /**
     * Adds the ChatMessage to the appropriate ActiveMQ queue
     *
     * @param chatMessage ChatMessage to add
     */
    private void sendChatMessageToProcessingQueue(ChatMessage chatMessage) {
        try {
            // Set up the connection and session
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("gateway");
            Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            // Create the queues and producers
            Queue requestsQueue = session.createQueue("MESSAGES");
            MessageProducer producer = session.createProducer(requestsQueue);
            producer.send(session.createObjectMessage(chatMessage));

            // Close everything once finished
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a SessionMessage on to the sessions module
     *
     * @param sessionMessage SessionMessage containing info about a user's current status
     */
    private void sendSessionMessage(SessionMessage sessionMessage) {
        // Sometimes the gateway will be null, and if it is, we add that to the message
        if (sessionMessage.getGateway() == null) {
            try {
                sessionMessage.setGateway(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        sendSessionMessageToProcessingQueue(sessionMessage);
    }

    /**
     * Adds the SessionMessage to the appropriate ActiveMQ queue
     *
     * @param sessionMessage SessionMessage to add
     */
    private void sendSessionMessageToProcessingQueue(SessionMessage sessionMessage) {
        try {
            // Creates the connection and session for the queueing system
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("gateway");
            Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            // Creates the queue, producer, and sends the SessionMessage
            Queue requestsQueue = session.createQueue("SESSIONS");
            MessageProducer producer = session.createProducer(requestsQueue);
            producer.send(session.createObjectMessage(sessionMessage));

            // Close all connections
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of ChatMessages for a given ChatLogRequest
     */
    private List<ChatMessage> getChatLog(ChatLogRequest chatLogRequest) {
        RestTemplate restTemplate = new RestTemplate();

        // Makes a GET request to message-service with the sender and receiver to receive conversation details about
        ResponseEntity<List<ChatMessage>> rateResponse =
                restTemplate.exchange("http://message-service:8080/history?sender=" + chatLogRequest.getRequestingUser() + "&receiver=" + chatLogRequest.getRequestedUser(),//todo make this address better
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<ChatMessage>>() {
                        });

        return rateResponse.getBody();
    }

    /**
     * Gets the list of all SessionMessages stored by the sessions module
     */
    private List<SessionMessage> getSessionsList() {
        RestTemplate restTemplate = new RestTemplate();

        // Creates a GET request to sessions, retrieving all sessions
        ResponseEntity<List<SessionMessage>> rateResponse =
                restTemplate.exchange("http://session:8080/sessions",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<SessionMessage>>() {
                        });

        return rateResponse.getBody();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }
}