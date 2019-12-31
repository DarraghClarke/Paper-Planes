package service.messageService;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class ChatLogService {
    private MongoCollection<UserMessage> collection;

    public ChatLogService() {
        try {
            MongoClient mongoClient = SingletonMongoClient.getInstance();
            MongoDatabase database = mongoClient.getDatabase("messages");
            collection = database.getCollection("messages", UserMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value="history")
    public List<UserMessage> getChatLog(@RequestParam String sender, @RequestParam String receiver) {
        List<UserMessage> result = collection.find(new BasicDBObject().append("sendTo", sender).append("sendFrom", receiver)).into(new ArrayList<>());
        result.addAll(collection.find(new BasicDBObject().append("sendTo", receiver).append("sendFrom", sender)).into(new ArrayList<>()));

        Collections.sort(result);
        return result;
    }
}
