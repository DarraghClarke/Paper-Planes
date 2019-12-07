package service.session;

import com.mongodb.*;

import java.net.UnknownHostException;

public class Server {
    public static void main(String[] args) {
        try {
            String host = "localhost";

            if (args[0] != null) {
                host = args[0];
            }

            MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://" + host + ":27017"));

            DB database = mongoClient.getDB("sessions");
            DBCollection collection = database.getCollection("sessions");

            DBObject person = new BasicDBObject()
                    .append("username", "oisinq")
                    .append("gateway", "192.168.1.1");

            collection.insert(person);

            DBCursor cursor = collection.find();

            System.out.println("We've got: " + cursor.size());

            while(cursor.hasNext()) {
                System.out.println(cursor.next().toString());
            }

            System.out.println(collection.find());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}
