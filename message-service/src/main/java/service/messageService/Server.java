package service.messageService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Server {
    public static void main(String[] args) {
        new Thread(new ReceivingMessagesThread()).start();
        SpringApplication.run(Server.class, args);
    }
}
