package service.messageService;

import com.mongodb.client.MongoCollection;
import message.ChatMessage;
import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Thread for handling ChatMessages from the MESSAGES queue
 */
public class ReceivingMessagesThread implements Runnable {
    private MongoCollection<ChatMessage> mongoCollection;

    /**
     * Method that is run when this thread is started
     */
    @Override
    public void run() {
        mongoCollection = SingletonMongoCollection.getInstance();

        // We need this sleep to ensure that the activeMQ server is fully setup before we try to access it.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Message message = getMessageFromQueue();
                if (isChatMessage(message)) {
                    ChatMessage response = getChatMessage(message);

                    mongoCollection.insertOne(response);
                    response.setProcessed(true);

                    RestTemplate restTemplate = new RestTemplate();
                    SessionMessage sessionMessage = restTemplate.getForObject("http://session:8080/sessions/"
                            + response.getSentBy(), SessionMessage.class);

                    MessageForwardingClient messageForwardingClient = new MessageForwardingClient(
                            new URI("ws://" + sessionMessage.getGateway() + ":8080/"), response);
                    Thread x = new Thread(messageForwardingClient);
                    x.start();
                } else {
                    System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Message getMessageFromQueue() {
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

            return consumer.receive();
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isChatMessage(Message message) {
        try {
            return message instanceof ObjectMessage && ((ObjectMessage) message).getObject() instanceof ChatMessage;
        } catch (JMSException e) {
            return false;
        }
    }

    private ChatMessage getChatMessage(Message message) {
        try {
            return (ChatMessage) ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }
}
