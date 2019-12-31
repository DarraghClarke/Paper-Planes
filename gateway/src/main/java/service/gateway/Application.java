package service.gateway;

import org.java_websocket.server.WebSocketServer;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Main class for the gateway module
 */
public class Application {
    public static void main(String[] args) throws UnknownHostException {
        String host = Inet4Address.getLocalHost().getHostAddress();
        int port = 8080;

        WebSocketServer server = new ChatEndpoint(new InetSocketAddress(host, port));
        server.run();
    }
}
