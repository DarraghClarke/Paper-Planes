package service.messageService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import message.ChatMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * SingletonMongoCollection – singleton class for provisioning a single MongoClient object across the system. Prevents multiple
 * connections across different threads and decreases memory usage
 */
public class SingletonMongoCollection {

    private static MongoCollection<ChatMessage> SINGLE_INSTANCE;

    private SingletonMongoCollection() {}

    /**
     * Returns the previously created MongoCollection instance or, if this does not exist, creates and returns a new MongoCollection.
     * @return the MongoCollection, ready for use
     */
    public static MongoCollection<ChatMessage> getInstance() {
        if (SINGLE_INSTANCE == null) {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).register(ChatMessage.class).build()));

            MongoClient client = new MongoClient("mongo:27017",
                    MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());

            SINGLE_INSTANCE = client.getDatabase("paper-planes").getCollection("messages", ChatMessage.class);
        }
        return SINGLE_INSTANCE;
    }
}