package service.session;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import message.SessionMessage;
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
            Thread.sleep(10000);
            //todo get rid of this hardcoding
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + "activemq" + ":61616");
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
                    if (content instanceof SessionMessage) {
                        SessionMessage response = (SessionMessage) content;

                        DBObject object = new BasicDBObject()
                                .append("user_id", response.getUsername())
                                .append("timestamp", response.getTimestamp())
                                .append("gateway", response.getGateway());

                        collection.insert(object);
                    }
                    // Finally, we acknowledge the message
                    message.acknowledge();

                    DBCursor cursor = collection.find();

                    System.out.println("Status update:\nWe've got: " + cursor.size());

                    while(cursor.hasNext()) {
                        System.out.println(cursor.next().toString());
                    }

                    System.out.println(collection.find());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
