package service.session;

import com.mongodb.*;

import message.SessionMessage;
import org.springframework.web.bind.annotation.*;


@RestController
public class SessionsService {
    private DBCollection collection;

    public SessionsService() {
        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + "mongo" + ":27017"));
            DB database = mongoClient.getDB("sessions");
            collection = database.getCollection("sessions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="{reference}",method=RequestMethod.GET)
    public SessionMessage getSession(@PathVariable("user_id") String userId) {
        DBObject object = collection.findOne(new BasicDBObject().append("user_id", userId));
        return new SessionMessage((long) object.get("user_id"), (String) object.get("timestamp"), (String) object.get("gateway"));
    }
}
