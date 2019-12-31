package message;

/**
 * ChatLogRequest – message passed between containers when a client wants the conversation history
 * between it and another user.
 */
public class ChatLogRequest extends Message {
    private String requestingUser;
    private String requestedUser;

    public ChatLogRequest() {
        super(Message.MessageTypes.CHAT_LOG_REQUEST);
    }

    /**
     * @param requestingUser The user who created the chat log request
     * @param requestedUser The user that requestingUser is chatting to
     */
    public ChatLogRequest(String requestingUser, String requestedUser){
        super(Message.MessageTypes.CHAT_LOG_REQUEST);
        this.requestingUser = requestingUser;
        this.requestedUser = requestedUser;
    }

    public void setRequestedUser(String requestedUser) {
        this.requestedUser = requestedUser;
    }

    public void setRequestingUser(String requestingUser) {
        this.requestingUser = requestingUser;
    }

    public String getRequestedUser() {
        return requestedUser;
    }

    public String getRequestingUser() {
        return requestingUser;
    }
}
