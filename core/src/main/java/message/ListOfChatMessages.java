package message;

import java.util.ArrayList;
import java.util.List;

/**
 * ListOfSessionMessages – message passed between gateway and client containing a list of all UserMessages in the system
 * between two users. It's the answer to a ChatLogRequest.
 */
public class ListOfChatMessages extends Message {
    private List<ChatMessage> messageList;
    private ChatLogRequest chatLogRequest;

    public ListOfChatMessages() {
        super(MessageTypes.LIST_OF_USER_MESSAGES);
        messageList = new ArrayList<>();
    }

    public ListOfChatMessages(List<ChatMessage> messageList) {
        super(MessageTypes.LIST_OF_USER_MESSAGES);
        this.messageList = messageList;
    }

    public ListOfChatMessages(List<ChatMessage> messageList, ChatLogRequest chatLogRequest) {
        super(MessageTypes.LIST_OF_USER_MESSAGES);
        this.messageList = messageList;
        this.chatLogRequest = chatLogRequest;
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    public ChatLogRequest getChatLogRequest() {
        return chatLogRequest;
    }

    public void setChatLogRequest(ChatLogRequest chatLogRequest) {
        this.chatLogRequest = chatLogRequest;
    }
}
