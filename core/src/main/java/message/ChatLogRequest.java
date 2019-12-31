package message;

public class ChatLogRequest extends Message {
    String requestingUser;
    String requestedUser;

    public ChatLogRequest() {
        super(MessageContainer.MessageTypes.CHAT_LOG_REQUEST);
    }

    public ChatLogRequest(String requestingUser, String requestedUser){
        super(MessageContainer.MessageTypes.CHAT_LOG_REQUEST);
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
