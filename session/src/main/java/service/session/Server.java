package service.session;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Server {
    public static void main(String[] args) {
        new Thread(new ReceivingSessionsThread()).start();
        SpringApplication.run(Server.class, args);
    }
}
