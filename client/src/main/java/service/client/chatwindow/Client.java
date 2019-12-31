package service.client.chatwindow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import message.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.client.login.LoginController;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//based on the sample implementation provided here: https://github.com/TooTallNate/Java-WebSocket/wiki#client-example

public class Client extends WebSocketClient {
    public static String username;
    public String userSelected;
    public String gateway;
    public Controller controller;


    public Client(URI serverURI, String username, Controller controller) {
        super(serverURI);
        this.username = username;
        this.controller = controller;
        gateway = serverURI.toString();
        controller.setClient(this);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //this changes the ui to the chatroom view
        LoginController.getInstance().changeScene();
        controller.setupUserlist();

        //this acts as one initial heartbeat before the timer stats
        Gson gson = new Gson();
        SessionMessage heartbeat = new SessionMessage(Instant.now().getEpochSecond(), username, null);
        String jsonStr = gson.toJson(heartbeat);
        send(jsonStr);

        //this hearbeat then sends hearbeats every 30 seconds
        Thread thread = new Thread(new Heartbeat(Client.this));
        thread.start();
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {

        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(message.Message.class, "type")
                .registerSubtype(SessionMessage.class, Message.MessageTypes.SESSION_MESSAGE)
                .registerSubtype(ChatMessage.class, Message.MessageTypes.USER_MESSAGE)
                .registerSubtype(ChatLogRequest.class, Message.MessageTypes.CHAT_LOG_REQUEST)
                .registerSubtype(ListOfChatMessages.class, Message.MessageTypes.LIST_OF_USER_MESSAGES)
                .registerSubtype(ListOfSessionMessages.class, Message.MessageTypes.LIST_OF_SESSION_MESSAGES);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        message.Message messageObj = gson.fromJson(message, message.Message.class);

        //this routes inbound messages based on type and then moves them to other methods
        switch (messageObj.getType()) {
            case Message.MessageTypes.USER_MESSAGE:
                ChatMessage chatMessage = (ChatMessage) messageObj;
                if (chatMessage.getSentBy().equals(userSelected)) {
                    controller.addToChat(chatMessage);
                }
                break;
            case Message.MessageTypes.LIST_OF_SESSION_MESSAGES:
                ListOfSessionMessages onlineStatus = (ListOfSessionMessages) messageObj;
                controller.setOnline(onlineStatus);
                break;
            case Message.MessageTypes.LIST_OF_USER_MESSAGES:
                ListOfChatMessages chatHistory = (ListOfChatMessages) messageObj;
                List<ChatMessage> historyMessageList = chatHistory.getMessageList();

                if (userSelected.equals(chatHistory.getChatLogRequest().getRequestedUser()) && controller.numberOfMessages() == 0) {
                    for (ChatMessage messages : historyMessageList) {
                        controller.addToChat(messages);
                    }
                }
                break;
        }

    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    /**
     * This takes in a text and constructs it into a message type and sends it on as JSON
     * @param msg a users message
     */
    public void sendMessage(String msg) {
        ChatMessage createChatMessage = new ChatMessage();
        createChatMessage.setSentBy(username);
        createChatMessage.setTimestamp(Instant.now().getEpochSecond());
        createChatMessage.setMessage(msg);
        createChatMessage.setSentTo(userSelected);
        controller.addToChat(createChatMessage);//this makes the message appear for the user in the chat panel

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String jsonStr = gson.toJson(createChatMessage);
        send(jsonStr);
        getSelectedUserChatHistory(userSelected);
    }

    public void setUserSelection(String selectedUser) {
        userSelected = selectedUser;
    }

    /**
     * This takes in a selected USER and creates a chatLogRequest to get the chat history
     * @param selectedUser The user you want the history with
     */
    public void getSelectedUserChatHistory(String selectedUser) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(new ChatLogRequest(username, selectedUser));
        send(jsonStr);
    }
}