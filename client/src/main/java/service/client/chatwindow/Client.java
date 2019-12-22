package service.client.chatwindow;

import java.io.*;
import java.net.URI;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.client.login.LoginController;
import message.Message;

//based on the sample implementation provided here: https://github.com/TooTallNate/Java-WebSocket/wiki#client-example

public class Client extends WebSocketClient {
    public static String username;
    public Controller controller;

    public Client(URI serverURI, String username, Controller controller) {
        super(serverURI);
        this.username = username;
        this.controller = controller;
        controller.setClient(this);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //this changes the ui to the chatroom view
        LoginController.getInstance().changeScene();
        System.out.println("new connection opened");
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        Gson gson = new Gson();
        Message msg = gson.fromJson(message, Message.class);
        controller.addToChat(msg);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }


    public void sendMessage(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setSender(username);
        createMessage.setTime(Instant.now());
        createMessage.setMessage(msg);

        controller.addToChat(createMessage);//this makes the message appear for the user in the chat panel
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(createMessage);
        send(jsonStr);
    }
}