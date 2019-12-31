package service.messageService;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import message.ChatMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ChatLogService – REST Controller for providing access to historical chat messages
 */
@RestController
public class ChatLogService {
    private MongoCollection<ChatMessage> collection;

    public ChatLogService() {
        // We access the collection for storing messages in the Mongo database
        collection = SingletonMongoCollection.getInstance();
    }

    /**
     * Endpoint for retrieving the chat history between two users (the order doesn't matter)
     */
    @GetMapping(value="history")
    public List<ChatMessage> getChatLog(@RequestParam String sender, @RequestParam String receiver) {

        List<ChatMessage> result = collection.find(new BasicDBObject().append("sentTo", receiver).append("sentBy", sender)).into(new ArrayList<>());
        result.addAll(collection.find(new BasicDBObject().append("sentTo", sender).append("sentBy", receiver)).into(new ArrayList<>()));

        // Since the ChatMessages are comparable, we can sort them easily.
        Collections.sort(result);
        return result;
    }
}
