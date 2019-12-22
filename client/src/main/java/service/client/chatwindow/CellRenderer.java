package service.client.chatwindow;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import message.SessionMessage;

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

                            Text username = new Text(user.getUsername());//atm just adds user name

                            hBox.getChildren().add(username);
                            hBox.setAlignment(Pos.CENTER_LEFT);

                            setGraphic(hBox);
                        }
                    }
                };
        return cell;
    }
}