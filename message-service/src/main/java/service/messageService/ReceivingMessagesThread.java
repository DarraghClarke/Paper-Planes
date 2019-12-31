package service.messageService;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.UserMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Thread for handling applications from the RESPONSES queue
 */
public class ReceivingMessagesThread implements Runnable {
    private MongoClient mongoClient;

    /**
     * Method that is run when this thread is started
     */
    @Override
    public void run() {
        mongoClient = SingletonMongoClient.getInstance();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("message-service");
            Session session = connection.createSession(false,
                    Session.CLIENT_ACKNOWLEDGE);

            connection.start();
            Queue queue = session.createQueue("MESSAGES");
            MessageConsumer consumer = session.createConsumer(queue);

            System.out.println("Connection Started");


            MongoDatabase database = mongoClient.getDatabase("paper-planes");
            MongoCollection<UserMessage> collection = database.getCollection("messages", UserMessage.class);

            while (true) {
                Message message = consumer.receive();
                if (message instanceof ObjectMessage) {
                    Object content = ((ObjectMessage) message).getObject();
                    if (content instanceof UserMessage) {
                        UserMessage response = (UserMessage) content;
                        System.out.println(response.getSentBy() + " -> " + response.getSentTo() + ": " + response.getMessage());
                        collection.insertOne(response);

                        RestTemplate restTemplate = new RestTemplate();
                        String gateway = restTemplate.getForObject("http://session:8080/sessions", String.class);

                        MessageForwardingClient webSocketClient = new MessageForwardingClient(new URI("ws://" + gateway + ":8080/"));
                        webSocketClient.sendUserMessage(response);
                    }
                } else {
                    System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
                }
            }
        } catch (JMSException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
