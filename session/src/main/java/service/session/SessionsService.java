package service.session;

import com.mongodb.*;

import message.Session;
import org.springframework.web.bind.annotation.*;


@RestController
public class SessionsService {
    private DBCollection collection;

    public SessionsService(String host) {
        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + host + ":27017"));
            DB database = mongoClient.getDB("sessions");
            collection = database.getCollection("sessions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="{reference}",method=RequestMethod.GET)
    public Session getSession(@PathVariable("user_id") String userId) {
        DBObject object = collection.findOne(new BasicDBObject().append("user_id", userId));
        return new Session((long) object.get("user_id"), (long) object.get("timestamp"), (String) object.get("gateway"));
    }
}
