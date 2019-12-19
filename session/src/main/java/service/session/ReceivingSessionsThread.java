package service.session;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import message.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Thread for handling applications from the RESPONSES queue
 */
public class ReceivingSessionsThread implements Runnable {
    private DBCollection collection;
    private String host;

    ReceivingSessionsThread(DBCollection collection, String host) {
        this.collection = collection;
        this.host = host;
    }

    /**
     * Method that is run when this thread is started
     */
    @Override
    public void run() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
            Connection connection = factory.createConnection();
            connection.setClientID("sessions");
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            Queue requestsQueue = session.createQueue("SESSIONS");
            MessageConsumer consumer = session.createConsumer(requestsQueue);

            // This while loop means the program is always listening for the next message
            while (true) {
                // We retrieve the message from the queue and check it is a ClientApplicationMessage
                Message message = consumer.receive();
                if (message instanceof ObjectMessage) {
                    Object content = ((ObjectMessage) message).getObject();
                    if (content instanceof Session) {
                        Session response = (Session) content;

                        DBObject object = new BasicDBObject()
                                .append("user_id", response.getUserId())
                                .append("timestamp", response.getTimestamp())
                                .append("gateway", response.getGateway());

                        collection.insert(object);
                    }
                    // Finally, we acknowledge the message
                    message.acknowledge();
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
