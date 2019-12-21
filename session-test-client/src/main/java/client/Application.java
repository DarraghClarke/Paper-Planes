package client;

import message.SessionMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.net.InetAddress;
import java.time.Instant;

public class Application {
    public static void main(String[] args) {
        try {
            Thread.sleep(10000);
            System.out.println("baby-client starting.");
            String host = "localhost";

            // todo: this is weird. it's passing in localhost. we want it to pass in "activemq"
            if (args.length > 0) {
                host = args[0];
            }

            System.out.println("host: " + host);

            host = "activemq";

            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
            Connection connection = factory.createConnection();
            connection.setClientID("baby-client");
            Session session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            connection.start();
            System.out.println("connected!");

            Queue requestsQueue = session.createQueue("SESSIONS");

            System.out.println("Queue created");
            MessageProducer producer = session.createProducer(requestsQueue);

            SessionMessage sessionMessage = new SessionMessage(Instant.now().getEpochSecond(), "oisinq-baby", InetAddress.getLocalHost().toString()); // (msg.getTime().getEpochSecond(), msg.getSender(), getAddress().toString()
            System.out.println("Sending message...");
            producer.send(session.createObjectMessage(sessionMessage));
            System.out.println("sent message");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
