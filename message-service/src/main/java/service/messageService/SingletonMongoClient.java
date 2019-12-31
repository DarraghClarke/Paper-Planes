package service.messageService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import message.ChatMessage;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class SingletonMongoClient {

    private static MongoClient SINGLE_INSTANCE;

    private SingletonMongoClient() {}

    public static MongoClient getInstance() {
        if (SINGLE_INSTANCE == null) {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).register(ChatMessage.class).build()));

            SINGLE_INSTANCE = new MongoClient("mongo:27017",
                    MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        }
        return SINGLE_INSTANCE;
    }
}