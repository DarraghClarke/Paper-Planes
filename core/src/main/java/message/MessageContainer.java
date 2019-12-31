package message;

import java.io.Serializable;

public class MessageContainer implements Serializable {
    private String messageType;
    private Message message;

    public MessageContainer(String messageType, Message message) {
        this.messageType = messageType;
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class MessageTypes {
        final public static String SESSION_MESSAGE = "SessionMessage";
        final public static String USER_MESSAGE = "UserMessage";
        final public static String CHAT_LOG_REQUEST = "ChatLongRequest";
    }
}
