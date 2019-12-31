package message;

/**
 * SessionMessage – Message that signifies the current status of a user. Used to track which gateway a client is connected
 * to and the latest time of activity. Also used to calculate if a user is actively using the system or if they're offline.
 */
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
