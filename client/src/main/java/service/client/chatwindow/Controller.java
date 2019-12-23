package service.client.chatwindow;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import message.Message;
import message.SessionMessage;
import service.client.messages.bubble.BubbleSpec;
import service.client.messages.bubble.BubbledLabel;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TextArea inputBox;
    @FXML
    private ListView userList;
    @FXML
    ListView chatPane;
    @FXML
    BorderPane borderPane;
    @FXML
    private Label userInfo;

    private String username;
    private Client client;


    public void sendButtonAction() throws IOException {
        if (!inputBox.getText().isEmpty()) {
            client.sendMessage(inputBox.getText());
            inputBox.clear();
        }
    }

    public synchronized void addToChat(Message msg) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                BubbledLabel label = new BubbledLabel();
                label.setText(msg.getMessage());
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                label.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                LocalDateTime time = LocalDateTime.ofInstant(msg.getTime(), ZoneId.systemDefault());
                hBox.getChildren().addAll(new Label(msg.getSender()), label, new Label(time.getHour() + ":" + time.getMinute()));
                return hBox;
            }
        };

        othersMessages.setOnSucceeded(event -> chatPane.getItems().add(othersMessages.getValue()));

        Task<HBox> localMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                hBox.setMaxWidth(chatPane.getWidth() - 20);
                hBox.setAlignment(Pos.TOP_RIGHT);
                BubbledLabel label = new BubbledLabel();
                label.setText(msg.getMessage());
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
                label.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                LocalDateTime time = LocalDateTime.ofInstant(msg.getTime(), ZoneId.systemDefault());
                hBox.getChildren().addAll(new Label(time.getHour() + ":" + time.getMinute()), label, new Label(msg.getSender()));
                return hBox;
            }
        };
        localMessages.setOnSucceeded(event -> chatPane.getItems().add(localMessages.getValue()));

        if (msg.getSender().equals(username)) {
            Thread thread = new Thread(localMessages);
            thread.setDaemon(true);
            thread.start();
        } else {
            Thread thread2 = new Thread(othersMessages);
            thread2.setDaemon(true);
            thread2.start();
        }
    }

    public void setUserInfo(SessionMessage user){
        if (System.currentTimeMillis() / 1000l - user.getTimestamp() > 60) {//i think this means last minute online
            userInfo.setText(user.getUsername() + "-Last online: " + (System.currentTimeMillis() / 1000l - user.getTimestamp())/60 + " minutes ago");
        } else{
            userInfo.setText(user.getUsername() + "-Online Now");
        }
    }

    public void setOnline(ArrayList<SessionMessage> allUsers) {
        Platform.runLater(() -> {
            ObservableList<SessionMessage> users = FXCollections.observableList(allUsers);
            userList.getItems().clear();
            userList.setItems(users);
            userList.setCellFactory(new CellRenderer());
            userList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SessionMessage>)
                    (observable, oldValue, newValue) -> {
                        System.out.println("Selected item: " + newValue.getUsername());
                        setUserInfo(newValue);
                    });
        });

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}