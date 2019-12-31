package service.messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.UserMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MessageForwardingClient extends WebSocketClient {
    public MessageForwardingClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {}

    @Override
    public void onMessage(String s) {}

    @Override
    public void onClose(int i, String s, boolean b) {}

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void sendUserMessage(UserMessage userMessage) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonStr = gson.toJson(userMessage);
        send(jsonStr);
    }
}
