package service.balancer;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import sun.net.util.IPAddressUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Balancer {

    List gateways= new ArrayList<URL>();

    @GetMapping("/getGateway")
    public URL getGateway(){

        return (URL) gateways.get(0);
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestParam URL gatewayInfo){

        gateways.add(gatewayInfo);
        System.out.println("added IP address" + gatewayInfo.toString());
    }
}
