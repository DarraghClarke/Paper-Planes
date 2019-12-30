package service.messageService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.Message;
import org.bson.Document;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class MessageService {
   public static List<Message>  messageList = new ArrayList<>();
   public static List<String>  receiverList = new ArrayList<>();

    public MessageService() {

    }

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        Receiver receiver = new Receiver();
        messageList = receiver.recieveMessages();
        //access db and forward
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("MESSAGES");

        for(Message message: messageList) {
            MongoCollection<Document> collection = database.getCollection(message.getSender());
            Document newDocumenet = new Document("sender", message.getSender())
                    .append("receiver", message.getReciever())
                    .append("message", message.getMessage())
                    .append("time", message.getTime());

         database.getCollection(message.getSender()).insertOne(newDocumenet);
        }

        forwardMessages(database);

    }

    public static List<String> getReceiver(List<Message> messages){
        for( Message message : messages){
            receiverList.add(message.getReciever());
            System.out.println("Message Received: " + message.getReciever());
        }
        return receiverList;
    }

    //forward messages to the correct gateway
    public static void  forwardMessages(MongoDatabase database)  {




    }


}
