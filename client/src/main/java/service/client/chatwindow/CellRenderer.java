package service.client.chatwindow;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_GREENPeer;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Callback;
import message.SessionMessage;

import java.time.Instant;

class CellRenderer implements Callback<ListView<SessionMessage>, ListCell<SessionMessage>> {
    @Override
    public ListCell<SessionMessage> call(ListView<SessionMessage> p) {

        ListCell<SessionMessage> cell =
                new ListCell<SessionMessage>() {

                    @Override
                    protected void updateItem(SessionMessage user, boolean bln) {
                        super.updateItem(user, bln);
                        setGraphic(null);
                        setText(null);
                        if (user != null) {
                            HBox hBox = new HBox();

                            Text username = new Text(user.getUsername() + "  ");//add user name and a space
                            Circle circle = new Circle(0, 0, 8);
                            if(Instant.now().getEpochSecond() - user.getTimestamp() > 60 ){
                                //online in the last minute
                                circle.setFill(Color.GRAY);
                            } else{
                                circle.setFill(Color.LIGHTGREEN);
                            }

                            hBox.getChildren().addAll(username,circle);
                            hBox.setAlignment(Pos.CENTER_LEFT);

                            setGraphic(hBox);
                        }
                    }
                };
        return cell;
    }
}