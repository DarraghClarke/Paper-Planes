package service.balancer;

import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Balancer {

    List gateways = new ArrayList<String>();

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
}
