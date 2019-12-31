package message;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static class MessageTypes {
        final public static String SESSION_MESSAGE = "SessionMessage";
        final public static String USER_MESSAGE = "UserMessage";
        final public static String CHAT_LOG_REQUEST = "ChatLongRequest";
    }
}
