package service.session;


import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.SessionMessage;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * SessionsService – REST Controller for accessing details about the sessions (i.e. the recent status) of a user in the system
 */
@RestController
public class SessionsService {
    private MongoCollection<SessionMessage> collection;

    public SessionsService() {
        try {
            MongoClient mongoClient = SingletonMongoCollection.getInstance();
            MongoDatabase database = mongoClient.getDatabase("paper-planes");
            collection = database.getCollection("sessions", SessionMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="sessions/{username}",method=RequestMethod.GET)
    public SessionMessage getSession(@PathVariable("username") String userId) {
        return collection.find(new BasicDBObject().append("username", userId)).first();
    }

    @RequestMapping(value="sessions",
            method=RequestMethod.GET)
    public List<SessionMessage> getSessions() {
        return collection.find().into(new ArrayList<>());
    }
}
