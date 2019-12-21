package service.session;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.SessionMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
        String host = "localhost";

        if (args.length > 0) {
            host = args[0];
        }

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).register(SessionMessage.class).build()));

        MongoClient mongoClient = new MongoClient("mongodb://" + "mongo" + ":27017",
                MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());

        MongoDatabase database = mongoClient.getDatabase("sessions");

        MongoCollection<SessionMessage> collection = database.getCollection("sessions", SessionMessage.class);

        new Thread(new ReceivingSessionsThread(collection, host)).start();
        SpringApplication.run(Server.class, args);
    }
}
