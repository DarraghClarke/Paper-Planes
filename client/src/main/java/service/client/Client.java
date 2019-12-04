package service.client;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.client.RestTemplate;

//based on the sample implementation provided here: https://github.com/TooTallNate/Java-WebSocket/wiki#client-example

public class Client extends WebSocketClient {

    public Client(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Scanner scanner = new Scanner(System.in);
        String readString = scanner.nextLine();
        send(readString);
        System.out.println("new connection opened");
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    public static void main(String[] args) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String temp = restTemplate.getForObject("http://localhost:8081/getGateway", String.class);
        System.out.println(temp);
        WebSocketClient client = new Client(new URI( "ws://"+temp+"/"));
        client.connect();
    }
}