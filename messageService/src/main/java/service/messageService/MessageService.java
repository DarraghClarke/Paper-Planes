package service.messageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import service.core.Message;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    List gateways = new ArrayList<String>();
    List messages = new ArrayList<Message>();



    public List<Message> getMessages(List<String> gateways){

        for(String gateway:gateways){
            Message message = new Message(gateway,gateway, gateway);
            messages.add(message);
        }

        return messages;
    }

    public void sendMessage(){

    }

    @GetMapping("/getGateway")
    public String getGateway() {
        //this just returns the first gateway  currently
        return gateways.get(0).toString();
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {

        gateways.add(gatewayInfo);
        System.out.println("added IP address " + gatewayInfo);
    }

    public static void main(String[] args) {

    }

}
