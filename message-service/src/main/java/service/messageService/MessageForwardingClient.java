package service.messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.UserMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class MessageForwardingClient extends WebSocketClient {
    private UserMessage userMessage;

    public MessageForwardingClient(URI serverUri, UserMessage userMessage) {
        super(serverUri);
        this.userMessage = userMessage;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("WebSocket connection opened!");
        sendUserMessage();
        close();
    }

    @Override
    public void onMessage(String s) {}

    @Override
    public void onClose(int i, String s, boolean b) {}

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    private void sendUserMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonStr = gson.toJson(userMessage);
        send(jsonStr);
    }
}
