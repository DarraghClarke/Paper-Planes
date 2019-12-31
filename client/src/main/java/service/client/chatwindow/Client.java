package service.client.chatwindow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import message.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import service.client.login.LoginController;

import java.net.URI;
import java.util.List;

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
        System.out.println("gateway host is " + gateway);
        controller.setClient(this);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //this changes the ui to the chatroom view
        LoginController.getInstance().changeScene();
        controller.setupUserlist();
//        System.out.println("new connection opened");
//        Gson gson= new Gson();
//        SessionMessage heartbeat=new SessionMessage(System.currentTimeMillis(),username,gateway);
//        String jsonStr = gson.toJson(heartbeat);
//        send(jsonStr);
//        System.out.println("we;ve done it again");
        Thread thread= new Thread(new Heartbeat(Client.this));
        thread.start();
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        //this is meant to be a logic to sort different types of messages
//        if (message.contains("\"sentBy\":")){//element unique to userMessage tyoe
//            UserMessage msg = gson.fromJson(message, UserMessage.class);
//            controller.addToChat(msg);
//        } else if (message.contains("\"gateway\":")){//element unique to session type
//            Type collectionType = new TypeToken<ArrayList<SessionMessage>>(){}.getType();
//            ArrayList<SessionMessage> msg = (ArrayList<SessionMessage>) gson.fromJson( message , collectionType);
//            System.out.println("wow?");
//            controller.setOnline(msg);
//        }

        RuntimeTypeAdapterFactory<Message> adapter = RuntimeTypeAdapterFactory
                .of(message.Message.class, "type")
                .registerSubtype(SessionMessage.class, Message.MessageTypes.SESSION_MESSAGE)
                .registerSubtype(UserMessage.class, Message.MessageTypes.USER_MESSAGE)
                .registerSubtype(ChatLogRequest.class, Message.MessageTypes.CHAT_LOG_REQUEST)
                .registerSubtype(ListOfUserMessages.class, Message.MessageTypes.LIST_OF_USER_MESSAGES)
                .registerSubtype(ListOfSessionMessages.class, Message.MessageTypes.LIST_OF_SESSION_MESSAGES);

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        System.out.println("new message: " + message);
        message.Message messageObj = gson.fromJson(message, message.Message.class);


        switch (messageObj.getType()) {
            case Message.MessageTypes.USER_MESSAGE:
                UserMessage userMessage = (UserMessage) messageObj;
                controller.addToChat(userMessage);
                break;
            case Message.MessageTypes.LIST_OF_SESSION_MESSAGES:
                ListOfSessionMessages onlineStatus = (ListOfSessionMessages) messageObj;
                System.out.println("wow?");
                controller.setOnline(onlineStatus);
                break;
            case Message.MessageTypes.LIST_OF_USER_MESSAGES:
                ListOfUserMessages chatHistory = (ListOfUserMessages) messageObj;
                List<UserMessage> historyMessageList= chatHistory.getMessageList();

                for(UserMessage messages: historyMessageList){
                    System.out.println("test");
                    controller.addToChat(messages);
                }
                break;
        }

    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }


    public void sendMessage(String msg) {
        UserMessage createUserMessage = new UserMessage();
        createUserMessage.setSentBy(username);
        createUserMessage.setTimestamp(System.currentTimeMillis());
        createUserMessage.setMessage(msg);
        createUserMessage.setSentTo(userSelected);
        controller.addToChat(createUserMessage);//this makes the message appear for the user in the chat panel
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // String jsonStr = gson.toJson(messageContainer);
        String jsonStr = gson.toJson(createUserMessage);
        send(jsonStr);
    }

    public void setUserSelection(String selectedUser){
        userSelected = selectedUser;
    }

    public void setSelectedUserChatHistory(String selectedUser){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(new ChatLogRequest(username,selectedUser));
        System.out.println("sent");
        send(jsonStr);
    }
}