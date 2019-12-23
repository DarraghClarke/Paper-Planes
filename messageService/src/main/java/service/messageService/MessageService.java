package service.messageService;

import message.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import service.gateway.ChatEndpoint;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    List gateways = new ArrayList<String>();
    List messages = new ArrayList<Message>();



    public List<Message> getMessages(List<Message> mesagesList){

        return messages;
    }

    public void sendMessage(){

    }

    @GetMapping("/getGateway")
    public ChatEndpoint getGateway() {
        ChatEndpoint gateway = new ChatEndpoint();
        return gateway;
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {

        gateways.add(gatewayInfo);
        System.out.println("added IP address " + gatewayInfo);
    }
//
//    public static void main(String[] args) {
//
//    }

}
