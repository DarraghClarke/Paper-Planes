package service.messageService;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import message.Message;
import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.bson.Document;
import service.session.SessionsService;

import javax.jms.*;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;



public class MessageService {
    public static List<Message> messageList = new ArrayList<>();
    public static List<String> receiverList = new ArrayList<>();

    public MessageService() {

    }

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        Receiver receiver = new Receiver();
        messageList = receiver.recieveMessages();
        //access db and forward
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        MongoDatabase database = mongoClient.getDatabase("MESSAGES");

        for (Message message : messageList) {
            MongoCollection<Document> collection = database.getCollection(message.getSender());
            Document newDocumenet = new Document("sender", message.getSender())
                    .append("receiver", message.getReciever())
                    .append("message", message.getMessage())
                    .append("time", message.getTime());

            collection.insertOne(newDocumenet);
        }

        getGateway(database);


    }

    public static List<String> getReceiver(List<Message> messages) {
        for (Message message : messages) {
            receiverList.add(message.getReciever());
            System.out.println("Message Received: " + message.getReciever());
        }
        return receiverList;
    }

    //forward messages to the correct gateway
    public static void getGateway(MongoDatabase database) {
        MongoIterable<String> collectionList = database.listCollectionNames();
        SessionsService session = new SessionsService();

        for (String collection : collectionList) {
            //get gateway
            BasicDBObject query = new BasicDBObject();
            query.put("sender", 1);
            DBCollection coll = (DBCollection) database.getCollection(collection);
            DBCursor cursor = coll.find(query);
            String sender = cursor.curr().get("sender").toString();
            SessionMessage gateway = session.getSession(sender);
            Message message = new Message();
            message.setTime((Instant) cursor.curr().get("time"));
            message.setSender(cursor.curr().get("sender").toString());
            message.setSender(cursor.curr().get("receiver").toString());
            message.setSender(cursor.curr().get("message").toString());

            sendMessage(gateway, message);


        }
    }


    public static void sendMessage(SessionMessage gateway, Message message) {
        try {
            Thread.sleep(10000);

            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("Message");
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            Queue requestsQueue = session.createQueue("MESSAGES");
            MessageProducer producer = session.createProducer(requestsQueue);

            //CONNECT TO THE CORRECT GATEWAY
            // websockets
            

            producer.send((javax.jms.Message) message);
            System.out.println("sent message");

            Thread.sleep(3000);
            System.out.println("rest time...");

        } catch (InterruptedException e) {
            e.printStackTrace();

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}

