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

        // This processes each message in the MESSAGES queue, and loops forever
        while (true) {
            Message message = getMessageFromQueue();

            if (isChatMessage(message)) {
                ChatMessage response = getChatMessage(message);

                // We insert the ChatMessage in a database so we can access it later
                mongoCollection.insertOne(response);
                // We set it to processed now that it is stored, so gateway knows to send it to a client instead of to
                // the MESSAGES queue again
                response.setProcessed(true);

                // We query sessions to get the gateway for the receiving user
                RestTemplate restTemplate = new RestTemplate();
                SessionMessage sessionMessage = restTemplate.getForObject("http://session:8080/sessions/"
                        + response.getSentBy(), SessionMessage.class);

                // We create a WebSocket to send this message to that gateway
                MessageForwardingClient messageForwardingClient = createWebSocketClient(sessionMessage.getGateway(), response);
                Thread webSocketThread = new Thread(messageForwardingClient);

                // Start the WebSocket thread
                webSocketThread.start();
            } else {
                System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
            }
        }
    }

    /**
     * Accesses the MESSAGES queue and consumes a message from the queue
     * @return the consumed message
     */
    private Message getMessageFromQueue() {
        try {
            // Set up the connection and session
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("message-service");
            Session session = connection.createSession(false,
                    Session.CLIENT_ACKNOWLEDGE);

            // Set up the queue, consumer, and consume the message
            connection.start();
            Queue queue = session.createQueue("MESSAGES");
            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive();

            message.acknowledge();

            // Close all connections
            connection.close();
            session.close();
            consumer.close();

            return message;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a JMS Message is a ChatMessage or not
     * @param message the JMS Message to check
     * @return true or false, if the Message is a ChatMessage or not
     */
    private boolean isChatMessage(Message message) {
        try {
            return message instanceof ObjectMessage && ((ObjectMessage) message).getObject() instanceof ChatMessage;
        } catch (JMSException e) {
            return false;
        }
    }

    /**
     * Cases the JMS Message into a ChatMessage
     * @param message the JMS Message to case
     * @return the obtained ChatMessage
     */
    private ChatMessage getChatMessage(Message message) {
        try {
            return (ChatMessage) ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a MessageForwardingClient to send the message to the gateway
     * @param gateway SessionMessage containing the appropriate gateway
     * @param chatMessage ChatMessage to send across the WebSocket
     * @return
     */
    private MessageForwardingClient createWebSocketClient(String gateway, ChatMessage chatMessage) {
        try {
            return new MessageForwardingClient(
                    new URI("ws://" + gateway + ":8080/"), chatMessage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
