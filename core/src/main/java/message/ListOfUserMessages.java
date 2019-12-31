package message;

import java.util.ArrayList;
import java.util.List;

public class ListOfUserMessages extends Message {
    private List<UserMessage> messageList;

    public ListOfUserMessages() {
        super(MessageTypes.LIST_OF_USER_MESSAGES);
        messageList = new ArrayList<>();
    }

    public ListOfUserMessages(List<UserMessage> messageList) {
        super(MessageTypes.LIST_OF_USER_MESSAGES);
        this.messageList = messageList;
    }

    public List<UserMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<UserMessage> messageList) {
        this.messageList = messageList;
    }
}
