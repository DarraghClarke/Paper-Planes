package service.session;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Thread for handling applications from the RESPONSES queue
 */
public class ReceivingSessionsThread implements Runnable {
    private MongoClient mongoClient;

    /**
     * Method that is run when this thread is started
     */
    @Override
    public void run() {
        mongoClient = SingletonMongoClient.getInstance();

        try {
            Thread.sleep(10000);
            //todo get rid of this hardcoding
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("sessions");
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();

            Queue requestsQueue = session.createQueue("SESSIONS");
            MessageConsumer consumer = session.createConsumer(requestsQueue);

            // This while loop means the program is always listening for the next message
            while (true) {
                MongoDatabase database = mongoClient.getDatabase("sessions");
                MongoCollection<SessionMessage> collection = database.getCollection("sessions", SessionMessage.class);

                // We retrieve the message from the queue and check it is a ClientApplicationMessage
                Message message = consumer.receive();
                if (message instanceof ObjectMessage) {
                    Object content = ((ObjectMessage) message).getObject();
                    if (content instanceof SessionMessage) {
                        SessionMessage response = (SessionMessage) content;
                        collection.insertOne(response);
                    }

                    // Finally, we acknowledge the message
                    message.acknowledge();

                    FindIterable<SessionMessage> cursor = collection.find();

                    System.out.println("Status update:\nWe've got: " + collection.countDocuments());

                    for (SessionMessage sessionMessage : cursor) {
                        System.out.println(sessionMessage.getUsername() + " - " + sessionMessage.getGateway()
                                + " - " + sessionMessage.getTimestamp());
                    }

                    System.out.println(collection.find());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
