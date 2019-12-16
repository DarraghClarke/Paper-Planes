package service.balancer;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class Balancer {

    List<String> gateways = new ArrayList<>();

    @GetMapping("/getGateway")
    public String getGateway() {
        if (gateways.isEmpty()) {
            throw new InternalError("No Gateways Available");
        } else {
            return findGatewayToReturn();
        }
    }

    public String findGatewayToReturn() {
        Random numberGenerator = new Random(System.currentTimeMillis());
        int random = 0;
        boolean returnedAlive = false;

        while (!returnedAlive) {
            random = numberGenerator.nextInt(gateways.size());
            try {
                returnedAlive = checkGatewayHealth(gateways.get(random).toString());
                if (!returnedAlive) {
                    //the gateway is dead so we should remove it and try again from the remaining list
                    gateways.remove(random);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return gateways.get(random);
    }

    public boolean checkGatewayHealth(String gateway) throws IOException {
        String[] ipAddress = gateway.split(":");//this seperates the ip address from the port
        InetAddress address = InetAddress.getByName(ipAddress[0]);
        return address.isReachable(10000);
    }

    @PostMapping("/addGateway")
    public void addGateway(@RequestBody String gatewayInfo) throws MalformedURLException {

        gateways.add(gatewayInfo);
        System.out.println("added IP address " + gatewayInfo);
    }
}
