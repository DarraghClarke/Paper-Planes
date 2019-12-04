package service.balancer;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import sun.net.util.IPAddressUtil;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Balancer {

    List gateways= new ArrayList<String>();

    @GetMapping("/getGateway")
    public String getGateway(){
        System.out.println("made it here");
        return  gateways.get(0).toString();
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {

        gateways.add(gatewayInfo);
        System.out.println("added IP address " + gatewayInfo);
    }
}
