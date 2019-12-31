package service.session;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.SessionMessage;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SessionsService {
    private MongoCollection<SessionMessage> collection;

    public SessionsService() {
        try {
            MongoClient mongoClient = SingletonMongoClient.getInstance();
            MongoDatabase database = mongoClient.getDatabase("paper-planes");
            collection = database.getCollection("sessions", SessionMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="sessions/{user_id}",method=RequestMethod.GET)
    public SessionMessage getSession(@PathVariable("user_id") String userId) {
        return collection.find(new BasicDBObject().append("username", userId)).first();
    }

    @RequestMapping(value="sessions",
            method=RequestMethod.GET)
    public List<SessionMessage> getSessions() {
        return collection.find().into(new ArrayList<>());
    }
}
