package service.messageService;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.LinkedList;

//recieves messages from Gateway and stores the messages in database
public class Receiver {
    public  LinkedList<message.Message> recieveMessages() throws InterruptedException {
        Thread.sleep(20000);
        String host = "localhost";
        String[] args = new String[0];
        LinkedList<message.Message> messageList = new LinkedList<message.Message>();
            if (args.length > 0) {
                host = args[0];
            }
            System.out.println("Starting Receiver on: " + host);

            try {
                ConnectionFactory factory =
                        new ActiveMQConnectionFactory("failover://tcp://" + host + ":61616");
                Connection connection = factory.createConnection();
                connection.setClientID("receiver");
                Session session = connection.createSession(false,
                        Session.CLIENT_ACKNOWLEDGE);
                Queue queue = session.createQueue("MESSAGES");
                MessageConsumer consumer = session.createConsumer(queue);


                connection.start();
                while (true) {
                    Message message = consumer.receive();
                    if (message instanceof message.Message) {
                        System.out.println(((message.Message) message).getMessage());
                        messageList.add((message.Message) message);

                    } else {
                        System.out.println("Unknown message type: " + message.getClass().getCanonicalName());
                    }


                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        return messageList;
    }



}
