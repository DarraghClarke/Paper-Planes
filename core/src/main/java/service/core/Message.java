package service.core;

import java.io.Serializable;

public class Message implements Serializable {

    public  String message;
    public String sentTo;
    public String sentBy;

    public Message(String message, String sentTo, String sentBy) {
        this.message = message;
        this.sentTo = sentTo; // ip address?
        this.sentBy = sentBy;


    }


}
