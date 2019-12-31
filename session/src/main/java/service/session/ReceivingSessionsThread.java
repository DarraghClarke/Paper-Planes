package service.session;

import com.mongodb.client.MongoCollection;
import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.bson.Document;

import javax.jms.*;

import static com.mongodb.client.model.Filters.eq;

/**
 * Thread for handling messages in the SESSIONS queue and updating the database
 */
public class ReceivingSessionsThread implements Runnable {
    private MongoCollection<SessionMessage> mongoCollection;

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

        // This processes each message in the SESSIONS queue, and loops forever
        while (true) {
            Message message = getMessageFromQueue();

            if (isSessionMessage(message)) {
                SessionMessage response = getSessionMessage(message);

                // Check if there's already an entry in sessions under this username
                SessionMessage existingSession = mongoCollection.find(eq("username", response.getUsername())).first();

                // If not, insert. Otherwise, update the existing entry with the latest timestamp
                if (existingSession == null) {
                    mongoCollection.insertOne(response);
                } else {
                    mongoCollection.updateOne(eq("username", response.getUsername()),
                            new Document("$set", new Document("timestamp", response.getTimestamp())));
                }
            } else {
                System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
            }
        }
    }

    /**
     * Reads from the SESSIONS queue and returns the message
     */
    private Message getMessageFromQueue() {
        try {
            // Set up the connection and session
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("sessions");
            Session session = connection.createSession(false,
                    Session.CLIENT_ACKNOWLEDGE);

            // Set up the queue, consumer, and consume the message
            connection.start();
            Queue queue = session.createQueue("SESSIONS");
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
     * Checks if a JMS Message is a SessionMessage or not
     * @param message the JMS Message to check
     * @return true or false, if the Message is a SessionMessage or not
     */
    private boolean isSessionMessage(Message message) {
        try {
            return message instanceof ObjectMessage && ((ObjectMessage) message).getObject() instanceof SessionMessage;
        } catch (JMSException e) {
            return false;
        }
    }

    /**
     * Cases the JMS Message into a SessionMessage
     * @param message the JMS Message to case
     * @return the obtained SessionMessage
     */
    private SessionMessage getSessionMessage(Message message) {
        try {
            return (SessionMessage) ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }
}
