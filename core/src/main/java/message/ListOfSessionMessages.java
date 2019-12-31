package message;

import java.util.ArrayList;
import java.util.List;

/**
 * ListOfSessionMessages – message passed between containers containing a list of all SessionMessages in the system:
 * used to display the user list in the client
 */
public class ListOfSessionMessages extends Message {
    private List<SessionMessage> messageList;

    public ListOfSessionMessages() {
        super(Message.MessageTypes.LIST_OF_SESSION_MESSAGES);
        messageList = new ArrayList<>();
    }

    public ListOfSessionMessages(List<SessionMessage> messageList) {
        super(Message.MessageTypes.LIST_OF_SESSION_MESSAGES);
        this.messageList = messageList;
    }

    public List<SessionMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<SessionMessage> messageList) {
        this.messageList = messageList;
    }
}
