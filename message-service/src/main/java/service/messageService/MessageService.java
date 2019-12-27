package service.messageService;

import message.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    List messages = new ArrayList<Message>();


    public MessageService(List<Message> receivedMessages) throws InterruptedException {
        Receiver receiver = new Receiver();
        receiver.recieveMessages();
        //access db and forward


    }







//    public List<Message> getMessages(List<Message> mesagesList){
//
//        return messages;
//    }
//
//    public void sendMessage(){
//
//    }

//    @GetMapping("/getGateway")
//    public ChatEndpoint getGateway() {
//        ChatEndpoint gateway = new ChatEndpoint();
//        return gateway;
//    }
//
//    @PostMapping("/addGateway")
//    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {
//
//        gateways.add(gatewayInfo);
//        System.out.println("added IP address " + gatewayInfo);
//    }
//


}
