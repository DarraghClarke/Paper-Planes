package message;

import java.io.Serializable;
import java.time.Instant;

public class Message implements Serializable {

    private String sender;
    private String message;
    private String reciever;
    private Instant time;

    public Message() {
    }
    
    public String  getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciever(){
        return reciever;
    }

    public void setReciever(String reciever){
        this.reciever = reciever;
    }

    public void setTime(Instant time){
        this.time=time;
    }

    public Instant getTime() {
        return time;
    }
}
