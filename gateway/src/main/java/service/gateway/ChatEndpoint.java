package service.gateway;

import java.net.*;
import java.nio.ByteBuffer;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;


//based on the sample implementation provided here: https://github.com/TooTallNate/Java-WebSocket/wiki#server-example

public class ChatEndpoint extends WebSocketServer {

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


        //sample code
        Message Response = new Message();
        Response.setMessage("returning message: "+msg.getMessage());
        Response.setSender("Gateway");
        Response.setReciever(msg.getSender());
        Response.setTime(Instant.now());

        Gson builder = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = builder.toJson(Response);
        conn.send(jsonStr);
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

    public void connectToLoadBalancer() {
        RestTemplate restTemplate = new RestTemplate();
        String gatewayAddress = getAddress().getHostString() + ":" + getAddress().getPort();
        HttpEntity<String> request = new HttpEntity<>(gatewayAddress);

        restTemplate.postForObject("http://localhost:8081/addGateway", request, String.class);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        WebSocketServer server = new ChatEndpoint(new InetSocketAddress(host, port));
        server.run();
    }
}