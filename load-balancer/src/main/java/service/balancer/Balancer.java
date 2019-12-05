package service.balancer;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class Balancer {

    List gateways = new ArrayList<String>();

    @GetMapping("/getGateway")
    public String getGateway() {
        return findGatewayToReturn();
    }

    public String findGatewayToReturn() {
        Boolean alive = false;
        String gatewayToReturn = null;
        int random = new Random(System.currentTimeMillis()).nextInt(gateways.size());

        try {
            alive = checkGatewayHealth(gateways.get(random).toString());
            gatewayToReturn = gateways.get(random).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!alive) {
            //the gateway is dead so we should remove it and try again from the remaining list
            gateways.remove(random);
            findGatewayToReturn();
        }
        //if its alive return the gateway
        return gatewayToReturn;
    }

    public Boolean checkGatewayHealth(String gateway) throws IOException {
        String[] ipAddress = gateway.split(":");//this seperates the ip address from the port
        InetAddress address = InetAddress.getByName(ipAddress[0]);
        Boolean reachable = address.isReachable(10000);
        return reachable;
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {

        gateways.add(gatewayInfo);
        System.out.println("added IP address " + gatewayInfo);
    }
}
