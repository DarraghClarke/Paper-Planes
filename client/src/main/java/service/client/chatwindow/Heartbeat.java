package service.client.chatwindow;

import com.google.gson.Gson;
import message.SessionMessage;

import java.util.Timer;
import java.util.TimerTask;

public class Heartbeat implements  Runnable{

        private Client client;

        public Heartbeat(Client client) {
            this.client = client;
        }

        public void run() {
            new Timer().schedule(
                    new TimerTask() {

                        @Override
                        public void run() {
                            Gson gson= new Gson();
                            // Gateway is null here because Client thinks the gateway to localhost. This isn't helpful,
                            // so gateway will figure this out
                            SessionMessage heartbeat = new SessionMessage(System.currentTimeMillis(),client.username,null);
                            String jsonStr = gson.toJson(heartbeat);
                            client.send(jsonStr);
                            System.out.println("we;ve done it again");
                        }
                    }, 0, 30000);
        }

}
