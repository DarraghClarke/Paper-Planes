package service.session;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.SessionMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@RestController
public class SessionsService {
    private MongoCollection<SessionMessage> collection;

    public SessionsService() {
        try {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).register(SessionMessage.class).build()));

            MongoClient mongoClient = new MongoClient("mongodb://" + "mongo" + ":27017",
                    MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
            MongoDatabase database = mongoClient.getDatabase("sessions");

            collection = database.getCollection("sessions", SessionMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="sessions/{reference}",method=RequestMethod.GET)
    public SessionMessage getSession(@PathVariable("user_id") String userId) {
        return collection.find(new BasicDBObject().append("user_id", userId)).first();
    }

    @RequestMapping(value="sessions",
            method=RequestMethod.GET)
    public List<SessionMessage> getSessions() {
        return collection.find().into(new ArrayList<>());
    }
}
