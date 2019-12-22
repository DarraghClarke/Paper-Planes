package service.gateway;

import java.net.*;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.Message;
import message.SessionMessage;
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

//based on the sample implementation provided here: https://github.com/TooTallNate/Java-WebSocket/wiki#server-example

public class ChatEndpoint extends WebSocketServer {
    MessageProducer producer;
    Session session;

    public ChatEndpoint(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //tbh gateway probably won't unpack this message, instead passing it on, but for now just to prove this works it will
        Gson gson = new Gson();
        Message msg = gson.fromJson(message, Message.class);
        System.out.println("received message from " + conn.getRemoteSocketAddress() + ": " + message);

//        try {
//            SessionMessage sessionMessage = new SessionMessage(msg.getTime().getEpochSecond(), msg.getSender(), getAddress().toString());
//            producer.send(session.createObjectMessage(sessionMessage));
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }

        //sample code
//        Message Response = new Message();
//        Response.setMessage("returning message: "+msg.getMessage());
//        Response.setSender("Gateway");
//        Response.setReciever(msg.getSender());
//        Response.setTime(Instant.now());
        // long timestamp, String username, String gateway
//
        List<SessionMessage> Response = new ArrayList<>();
        Response.add(new SessionMessage(19l,"big Tom","this gateway idk"));
        Response.add(new SessionMessage(29l,"GIT","this gateway idk"));
//        try {
//            SessionMessage sessionMessage = new SessionMessage(Instant.now().getEpochSecond(), "oisinq-baby", InetAddress.getLocalHost().toString()); // (msg.getTime().getEpochSecond(), msg.getSender(), getAddress().toString()
//            System.out.println("Sending message...");
//            producer.send(session.createObjectMessage(sessionMessage));
//            System.out.println("sent message");
//
//            Thread.sleep(3000);
//
//            System.out.println("rest time...");
//            RestTemplate restTemplate = new RestTemplate();
//
//            ResponseEntity<List<SessionMessage>> rateResponse =
//                    restTemplate.exchange("http://session:8080/sessions",
//                            HttpMethod.GET, null, new ParameterizedTypeReference<List<SessionMessage>>() {
//                            });
//            System.out.println("Code: " + rateResponse.getStatusCodeValue());
//            List<SessionMessage> Response = rateResponse.getBody();
//            System.out.println("There are " + Response.size() + " messages. Neat.");
//            for (SessionMessage message2 : Response) {
//                System.out.println("Content: " + message2.getTimestamp() + " - " + message2.getUsername() + " - " + message2.getGateway());
//            }
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            String jsonStr = builder.toJson(Response);
            conn.send(jsonStr);
//        } catch (Exception ex) {
//            System.out.println("hi");
//        }
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
        try {
            connectToLoadBalancer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.print("server started successfully");

        String host = "localhost";

//        try {
//            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
//            Connection connection = factory.createConnection();
//            connection.setClientID("sessions");
//            session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
//            connection.start();
//
//            Queue requestsQueue = session.createQueue("SESSIONS");
//            producer = session.createProducer(requestsQueue);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
    }

    public void connectToLoadBalancer() throws UnknownHostException {
        RestTemplate restTemplate = new RestTemplate();
        String gatewayAddress = getAddress().getHostString() + ":" + getAddress().getPort();
        HttpEntity<String> request = new HttpEntity<>(gatewayAddress);

//        try {
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    //    restTemplate.postForObject("http://192.168.99.100:8081/addGateway", request, String.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        String host = Inet4Address.getLocalHost().getHostAddress();
        int port = 8080;

        System.out.println("waited");
        WebSocketServer server = new ChatEndpoint(new InetSocketAddress(host, port));
        server.run();
    }
}