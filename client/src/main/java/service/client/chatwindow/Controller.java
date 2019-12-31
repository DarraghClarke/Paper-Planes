package service.client.chatwindow;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import message.ChatMessage;
import message.ListOfSessionMessages;
import message.SessionMessage;
import service.client.messages.bubble.BubbleSpec;
import service.client.messages.bubble.BubbledLabel;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private String selectedUser = "";


    /**
     * This method sends the user message through client and clears the chat box
     */
    public void sendAction() {
        if (!inputBox.getText().isEmpty()) {
            client.sendMessage(inputBox.getText());
            inputBox.clear();
        }
    }

    /**
     * This message takes in a Chat message and then depending on weather it was send by the user
     * or a different user, it displays it in the chat panel as a bubble with username, timestamp and message.
     *
     * @param msg a ChatMessage object
     */
    public synchronized void addToChat(ChatMessage msg) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() {
                HBox hBox = new HBox();
                BubbledLabel label = new BubbledLabel();
                label.setText(msg.getMessage());
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                label.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                Instant instant = Instant.ofEpochSecond(msg.getTimestamp());
                LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                hBox.getChildren().addAll(new Label(msg.getSentBy()), label, new Label((time.format(DateTimeFormatter.ofPattern("HH:mm")))));//todo check time label

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
                Instant instant = Instant.ofEpochSecond(msg.getTimestamp());
                LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                hBox.getChildren().addAll(new Label((time.format(DateTimeFormatter.ofPattern("HH:mm")))), label, new Label(msg.getSentBy()));//todo check time label
                return hBox;
            }
        };
        localMessages.setOnSucceeded(event -> chatPane.getItems().add(localMessages.getValue()));

        if (msg.getSentBy().equals(username)) {
            Thread thread = new Thread(localMessages);
            thread.setDaemon(true);
            thread.start();
        } else {
            Thread thread2 = new Thread(othersMessages);
            thread2.setDaemon(true);
            thread2.start();
        }
    }

    /**
     * This takes in a session Message and uses time stamps of users to display them as offline/online on the top panel
     * @param user a sessionMessage type
     */
    public void setUserInfo(SessionMessage user) {
        if (Instant.now().getEpochSecond() - user.getTimestamp() > 60) {//i think this means last minute online
            userInfo.setText(user.getUsername() + ": Last online " + (Instant.now().getEpochSecond() - user.getTimestamp()) / 60 + " minutes ago");
        } else {
            userInfo.setText(user.getUsername() + ": Online Now");
        }
    }

    /**
     * This is used to set the users online, this involves deleting the current list and adding in the new ones,
     * it also keeps track of which user is selected
     * @param allUsers takes in a list of session messages which represents users
     */
    public void setOnline(ListOfSessionMessages allUsers) {
        Platform.runLater(() -> {
            ObservableList<SessionMessage> users = FXCollections.observableList(allUsers.getMessageList());
            int selectedIndex = userList.getSelectionModel().getSelectedIndex();
            userList.getItems().clear();
            userList.setItems(users);
            userList.getSelectionModel().select(selectedIndex);
            userList.setCellFactory(new CellRenderer());
            userList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<SessionMessage>)
                    (observable, oldValue, newValue) -> {
                        if (oldValue != null) {
                            if (newValue != null && !newValue.getUsername().equals(selectedUser)) {
                                selectedUser = newValue.getUsername();
                                updateUI(newValue);
                            }
                        } else {//this is a special case for the very first time this is selected
                            if (newValue != null && !newValue.getUsername().equals(selectedUser)) {
                                selectedUser = newValue.getUsername();
                                updateUI(newValue);
                            }
                        }
                    });
        });

    }

    /**
     * This takes in a new selected user and then updates the individual elements of the UI from that
     *
     * @param newValue the new selected user
     */
    private void updateUI(SessionMessage newValue) {
        inputBox.clear();
        inputBox.editableProperty().setValue(true);
        inputBox.setPromptText("Enter message to " + newValue.getUsername() + " here...");
        chatPane.getItems().clear();
        client.getSelectedUserChatHistory(newValue.getUsername());
        setUserInfo(newValue);
        client.setUserSelection(newValue.getUsername());
    }

    /**
     * This is used to do some initial setup of userlist while it awaits updates from the gateway
     */
    public void setupUserlist() {
        userList.getItems().add("Connecting to server");
        inputBox.deselect();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This listens to key inputs and routes ENTER key hits to send messages
     * and SHIFT+ENTER combo to act to do new line
     * @param event
     * @throws IOException
     */
    public void sendMethod(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
            event.consume();
            sendAction();
        } else if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
            int tmp = inputBox.getCaretPosition();
            inputBox.setText(inputBox.getText() + "\n");
            inputBox.positionCaret(tmp + 1);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public int numberOfMessages() {
        return chatPane.getItems().size();
    }
}