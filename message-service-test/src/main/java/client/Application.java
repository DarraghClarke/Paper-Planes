package main.java.client;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;

public class Application {
    public static void main(String[] args) {
        try {
            Thread.sleep(20000);
            System.out.println("test-client starting.");
            String host = "localhost";

            if (args.length > 0) {
                host = args[0];
            }

            System.out.println("host: " + host);

            host = "activemq";

            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
            Connection connection = factory.createConnection();
            connection.setClientID("test-client");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            connection.start();
            System.out.println("connected!");

            Queue requestsQueue = session.createQueue("MESSAGES");

            System.out.println("Queue created");
            MessageProducer producer = session.createProducer(requestsQueue);

            message.Message message = new message.Message();
            message.setMessage("hello");
            message.setReciever("Sinead");
            message.setSender("Cooper");
            message.setTime(Instant.now());
            System.out.println("Sending message...");
            producer.send((Message) message);
            System.out.println("sent message");

            Thread.sleep(3000);

            System.out.println("rest time...");



       } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
