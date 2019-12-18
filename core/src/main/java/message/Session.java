package message;

public class Session {
    public long timestamp;
    public long userId;
    public String gateway;


    public Session(long timestamp, long userId, String gateway) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.gateway = gateway;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
}
