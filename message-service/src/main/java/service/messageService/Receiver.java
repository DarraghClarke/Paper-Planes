package service.messageService;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.LinkedList;

//recieves messages from Gateway and stores the messages in database
public class Receiver {
    public  LinkedList<message.Message> receiveMessages() throws InterruptedException {
        Thread.sleep(20000);
        String host = "localhost";
        String[] args = new String[0];
        LinkedList<message.Message> messageList = new LinkedList<message.Message>();
            if (args.length > 0) {
                host = args[0];
            }
            System.out.println("Starting Receiver on: " + host);
                host = "activemq";

            try {

                ConnectionFactory factory =
                        new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
                Connection connection = factory.createConnection();
                connection.setClientID("receiver");
                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);


                System.out.println("Connection Started");
                Queue queue = session.createQueue("MESSAGES");
                MessageConsumer consumer = session.createConsumer(queue);
                connection.start();

                while (true) {
               Message message = consumer.receive();
                    if (message instanceof ObjectMessage) {
                        Object content = ((ObjectMessage) message).getObject();
                        if (content instanceof message.Message) {
                            message.Message response = (message.Message) content;
                            System.out.println((response));
                            messageList.add((message.Message) message);
                            message.acknowledge();

                        }
                    } else {
                        System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
                    }

                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        return messageList;
    }



}
