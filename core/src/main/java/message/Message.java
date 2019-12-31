package message;

import java.io.Serializable;

/**
 * Message – This is the base object passed between the different containers in the system. This makes deserializing
 * objects easier, especially when using WebSockets where you don't explicitly know the associated class
 * with the deserialized object
 */
public class Message implements Serializable {
    private String type;

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * MessageTypes – a static class of final Strings used to ensure consistency of message types in the system
     */
    public static class MessageTypes {
        final public static String SESSION_MESSAGE = "SessionMessage";
        final public static String USER_MESSAGE = "ChatMessage";
        final public static String LIST_OF_SESSION_MESSAGES = "ListOfSessionMessages";
        final public static String LIST_OF_USER_MESSAGES = "ListOfUserMessagesMessage";
        final public static String CHAT_LOG_REQUEST = "ChatLongRequest";
    }
}
