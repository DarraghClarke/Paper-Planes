package service.session;

import com.mongodb.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.UnknownHostException;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
        String host = "localhost";

        if (args.length > 0) {
            host = args[0];
        }

        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + host + ":27017"));


            DB database = mongoClient.getDB("sessions");
            DBCollection collection = database.getCollection("sessions");

            new Thread(new ReceivingSessionsThread(collection, host)).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        SpringApplication.run(Server.class, args);
    }
}
