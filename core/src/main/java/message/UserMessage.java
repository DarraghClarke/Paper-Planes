package message;

public class UserMessage extends Message implements Comparable<UserMessage> {
    private String message;
    private String sentTo;
    private String sentBy;
    private long timestamp;
    private boolean processed;

    public UserMessage() {
        super(Message.MessageTypes.USER_MESSAGE);
        processed = false;
    }

    public UserMessage(String message, String sentTo, String sentBy) {
        super(Message.MessageTypes.USER_MESSAGE);
        this.message = message;
        this.sentTo = sentTo; // ip address?
        this.sentBy = sentBy;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy(){
        return sentBy;
    }

    public void setSentBy(String sentBy){
        this.sentBy = sentBy;
    }

    public void setTimestamp(long timestamp){
        this.timestamp=timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public int compareTo(UserMessage o) {
        return (int)(timestamp - o.getTimestamp());
    }
}
