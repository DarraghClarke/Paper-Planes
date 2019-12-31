package service.messageService;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.Message;
import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.bson.Document;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;


public class MessageService {
    public static List<Message> messageList = new ArrayList<>();

    public MessageService() {

    }

    public static void main(String[] args)  {

        try {
            Receiver receiver = new Receiver();
            messageList = receiver.receiveMessages();
            //access db and forward
                MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
                MongoDatabase database = mongoClient.getDatabase("messages");
                MongoCollection<Message> collection = database.getCollection("messages", Message.class);
                populateDatabase(collection, mongoClient);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

        public static void populateDatabase (MongoCollection < Message > messages, MongoClient mongoClient){
            try {
                Thread.sleep(10000);
                ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
                Connection connection = factory.createConnection();
                connection.setClientID("messages");
                javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
                connection.start();

                Queue requestsQueue = session.createQueue("MESSAGES");
                MessageConsumer consumer = session.createConsumer(requestsQueue);


                while (true) {
                    MongoDatabase database = mongoClient.getDatabase("messages");
                    MongoCollection<Message> collection = database.getCollection("messages", Message.class);


                    javax.jms.Message message = consumer.receive();
                    if (message instanceof ObjectMessage) {
                        Object content = ((ObjectMessage) message).getObject();
                        if (content instanceof Message) {
                            Message response = (Message) content;
                            Document query = new Document();
                            query.append("_id", "messages");
                            Document setData = new Document();
                            setData.append("sender", response.getSender()).append("time", response.getTime()).append("receiver", response.getReciever()).append("message", response.getMessage());
                            Document update = new Document();
                            update.append("$set", setData);
                            collection.updateOne(query, update);

                            message.acknowledge();
                            getGateway(collection, response);
                        }
                    }
                }
            }

        catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JMSException e) {
                e.printStackTrace();

            }
        }


        //forward messages to the correct gateway
        public static void getGateway (MongoCollection<Message> database, Message response){
//            set up a RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            Document query = new Document("sender", 1);
            String username = "";
//          String username =  database.find(query, response.getSender()).first();
            Object getObject = restTemplate.getForObject("http://session:8081/ " + username, String.class);

//            send a GET request to "session:8080/sessions/username-goes-here"
//            access the gateway variable of the returned object.


        }


        public static void sendMessage (SessionMessage gateway, Message message){

                //CONNECT TO THE CORRECT GATEWAY
                // websockets


        }
    }



