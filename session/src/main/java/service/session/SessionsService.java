package service.session;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import message.SessionMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
            collection = SingletonMongoCollection.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the SessionMessage object for a given user
     * @param username The user to lookup
     * @return The corresponding SessionMessage
     */
    @RequestMapping(value="sessions/{username}",method=RequestMethod.GET)
    public SessionMessage getSession(@PathVariable("username") String username) {
        return collection.find(new BasicDBObject().append("username", username)).first();
    }

    /**
     * Gets all SessionMessage objects in the database
     * @return all SessionMessage objects
     */
    @RequestMapping(value="sessions",
            method=RequestMethod.GET)
    public List<SessionMessage> getSessions() {
        return collection.find().into(new ArrayList<>());
    }
}
