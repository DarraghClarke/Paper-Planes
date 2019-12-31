package message;

/**
 * ChatMessage – This is a chat message sent between two users.
 * It's Comparable to help make sorting messages easier
 */
public class ChatMessage extends Message implements Comparable<ChatMessage> {
    private String message;
    private String sentTo;
    private String sentBy;
    private long timestamp;

    // "processed" signifies if the ChatMessage has been processed by the system yet – if the message is inbound
    // or outbound in the system
    private boolean processed;

    public ChatMessage() {
        super(Message.MessageTypes.USER_MESSAGE);
        processed = false;
    }

    public ChatMessage(String message, String sentTo, String sentBy) {
        super(Message.MessageTypes.USER_MESSAGE);
        this.message = message;
        this.sentTo = sentTo; // ip address?
        this.sentBy = sentBy;
        processed = false;
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

    /*
     * This is used to compare two ChatMessages to see which is newest. Used in sorting.
     */
    @Override
    public int compareTo(ChatMessage o) {
        return (int)(timestamp - o.getTimestamp());
    }
}
