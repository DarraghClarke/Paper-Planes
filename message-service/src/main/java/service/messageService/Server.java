package service.messageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * Main class for message-service
 */
@SpringBootApplication(exclude = MongoAutoConfiguration.class) // Excluded to prevent a Spring Boot error
public class Server {
    public static void main(String[] args) {
        // We create a Thread for receiving messages, and also start up the REST endpoints
        new Thread(new ReceivingMessagesThread()).start();
        SpringApplication.run(Server.class, args);
    }
}
