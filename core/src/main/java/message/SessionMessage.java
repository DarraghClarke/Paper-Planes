package message;

public class SessionMessage extends Message {
    private long timestamp;
    private String username;
    private String gateway;

    public SessionMessage() {
        super(Message.MessageTypes.SESSION_MESSAGE);
    }

    public SessionMessage(long timestamp, String username, String gateway) {
        super(Message.MessageTypes.SESSION_MESSAGE);
        this.timestamp = timestamp;
        this.username = username;
        this.gateway = gateway;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
