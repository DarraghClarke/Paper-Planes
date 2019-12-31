package service.client.chatwindow;

import com.google.gson.Gson;
import message.SessionMessage;

import java.time.Instant;
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
                            SessionMessage heartbeat = new SessionMessage(Instant.now().getEpochSecond(),client.username,null);
                            String jsonStr = gson.toJson(heartbeat);
                            client.send(jsonStr);
                        }
                    }, 0, 15000);
        }

}
