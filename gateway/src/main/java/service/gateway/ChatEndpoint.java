package service.gateway;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{username}")//, decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {
    private Session session;
    private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        //when we get a new user
    }

    @OnMessage
    public void onMessage() throws IOException, EncodeException {
        //do something with input
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        //do something
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

}
