package service.session;

import com.mongodb.*;
import message.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.UnknownHostException;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
        try {
            String host = "localhost";

            if (args.length > 0) {
                host = args[0];
            }

            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + host + ":27017"));

            DB database = mongoClient.getDB("sessions");
            DBCollection collection = database.getCollection("sessions");
            collection.setObjectClass(Session.class);

            new Thread(new ReceivingSessionsThread(collection, host)).start();
            SpringApplication.run(Server.class, args);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
