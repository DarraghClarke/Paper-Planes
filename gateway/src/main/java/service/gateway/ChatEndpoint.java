package service.gateway;
import java.net.*;
import java.nio.ByteBuffer;

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
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        conn.send("returning message you sent : "+message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        try {
            connectToLoadBalancer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("server started successfully");
    }

    public void connectToLoadBalancer() throws Exception{

//        URL obj = new URL("http://localhost:8081/addGateway");
//        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
//        postConnection.setRequestMethod("POST");
//        postConnection.setRequestProperty("Content-Type", "application/json; utf-8");
//        postConnection.setRequestProperty("Accept", "application/json");
//        postConnection.setDoOutput(true);
//        String jsonInputString = "{\"ipaddress\": \""+getAddress().toString()+"\", \"job\": \"Programmer\"}";
//        try(OutputStream os = postConnection.getOutputStream()) {
//            byte[] input = jsonInputString.getBytes("utf-8");
//            System.out.println(input);
//            os.write(input, 0, input.length);
//        }


        RestTemplate restTemplate = new RestTemplate();
        InetSocketAddress a=getAddress();
        System.out.println(a);
        URL gatewayAddress = new URL("http://"+a.toString());

        HttpEntity<URL> request = new HttpEntity<>(gatewayAddress);

        restTemplate.postForObject("http://localhost:8081/addGateway", request, URL.class);

        //System.out.println(foo);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        WebSocketServer server = new ChatEndpoint(new InetSocketAddress(host, port));
        server.run();
    }
}