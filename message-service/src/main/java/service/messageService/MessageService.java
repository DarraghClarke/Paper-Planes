package service.messageService;

import message.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
   public static List<Message>  messageList = new ArrayList<>();
   public static List<String>  gatewayList = new ArrayList<>();

    public MessageService() {

    }

    public static void main(String[] args) throws InterruptedException {
        Receiver receiver = new Receiver();
        messageList = receiver.recieveMessages();
        //access db and forward
        getReceiver(messageList);
        forwardMessages(messageList);

    }

    public static List<String> getReceiver(List<Message> messages){
        for( Message message : messages){
            gatewayList.add(message.getReciever());
            System.out.println(message.getReciever());
        }
        return gatewayList;
    }

    public static void forwardMessages(List<Message> messages){
        for( Message message : messages){

        }
    }


}
