package service.messageService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import message.ChatMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocketClient for forwarding messages on to the gateway
 */
public class MessageForwardingClient extends WebSocketClient {
    private ChatMessage chatMessage;

    public MessageForwardingClient(URI serverUri, ChatMessage chatMessage) {
        super(serverUri);
        this.chatMessage = chatMessage;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        // When the connection opens, we want to send the user message instantly
        sendUserMessage();
    }

    @Override
    public void onMessage(String s) {}

    @Override
    public void onClose(int i, String s, boolean b) {}

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    /**
     * Sends the ChatMessage to the gateway via a WebSocket connection
     */
    private void sendUserMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonStr = gson.toJson(chatMessage);
        send(jsonStr);
    }
}
