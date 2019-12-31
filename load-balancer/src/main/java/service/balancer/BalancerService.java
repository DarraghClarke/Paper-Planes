package service.balancer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BalanacerService – REST Controller used to provide clients with a gateway to connect to
 */
@RestController
public class BalancerService {

    private List<String> gateways = new ArrayList<>();

    /**
     * Returns a random gateway to the client. This distributes the clients across all of the gateways.
     * This isn't the most effective load balancer, but in general it distributes the load across all the gateways.
     * @return A String representing the IP address of the gateway
     */
    @GetMapping("/gateways/random")
    public String getRandomGateway() {
        if (gateways.isEmpty()) {
            throw new InternalError("No gateways added to the system. Try again later.");
        } else {
            return findGatewayToReturn();
        }
    }

    /**
     * Adds a gateway to the system, so it can be forwarded to a client.
     * @param gatewayAddress IP address of gateway
     */
    @PostMapping("/gateways")
    public void addGateway(@RequestBody String gatewayAddress) {
        gateways.add(gatewayAddress);
    }

    /**
     * Selects a random gateway and returns it to the user
     * @return
     */
    private String findGatewayToReturn() {
        Random numberGenerator = new Random(System.currentTimeMillis());
        int randomIndex = -1;
        boolean aliveGatewaySelected = false;

        while (!aliveGatewaySelected) {
            // Selects the index of a gateway
            randomIndex = numberGenerator.nextInt(gateways.size());

            try {
                // Checks if the selected gateway is actually alive
                aliveGatewaySelected = checkGatewayHealth(gateways.get(randomIndex));
                if (!aliveGatewaySelected) {
                    //the gateway is dead so we should remove it and try again from the remaining list
                    gateways.remove(randomIndex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Returns the randomly selected gateway
        return gateways.get(randomIndex);
    }

    /**
     * Checks if a gateway is online or not
     */
    private boolean checkGatewayHealth(String gateway) throws IOException {
        String[] ipAddress = gateway.split(":"); // This separates the IP Address from the port
        InetAddress address = InetAddress.getByName(ipAddress[0]);

        return address.isReachable(10000);
    }
}
