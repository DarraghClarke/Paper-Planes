package service.messageService;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.ChatMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class ChatLogService {
    private MongoCollection<ChatMessage> collection;

    public ChatLogService() {
        try {
            MongoClient mongoClient = SingletonMongoClient.getInstance();
            MongoDatabase database = mongoClient.getDatabase("paper-planes");
            collection = database.getCollection("messages", ChatMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value="history")
    public List<ChatMessage> getChatLog(@RequestParam String sender, @RequestParam String receiver) {
        System.out.println("Sender is " + sender + ", receiver is " + receiver);
        System.out.println("We've got " + collection.countDocuments() + " documents. Cool.");

        List<ChatMessage> result = collection.find(new BasicDBObject().append("sentTo", receiver).append("sentBy", sender)).into(new ArrayList<>());
        result.addAll(collection.find(new BasicDBObject().append("sentTo", sender).append("sentBy", receiver)).into(new ArrayList<>()));

        Collections.sort(result);
        System.out.println("We've got " + result.size() + " results...");
        return result;
    }
}
