package client;

import message.UserMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class Application {
    public static void main(String[] args) {
        try {
            String host = "activemq";
            System.out.println("host: " + host);

            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
            Connection connection = factory.createConnection();
            connection.setClientID("message-test-client");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            System.out.println("connected!");

            Queue requestsQueue = session.createQueue("MESSAGES");

            System.out.println("Queue created");
            MessageProducer producer = session.createProducer(requestsQueue);

            UserMessage message = new UserMessage();
            message.setMessage("hello");
            message.setSentTo("Sinead");
            message.setSentBy("Cooper");
            message.setTimestamp(Instant.now().getEpochSecond());
            System.out.println("Sending message...");
            producer.send(session.createObjectMessage(message));
            System.out.println("sent message");

            Thread.sleep(3000);

            System.out.println("rest time...");
       } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
