package service.client.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.client.chatwindow.Client;
import service.client.chatwindow.Controller;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameTextfield;
    public static Controller controller;
    private Scene scene;
    private static LoginController instance;

    public LoginController() {
        instance = this;
    }

    public static LoginController getInstance() {
        return instance;
    }

    public void loginButtonAction() throws IOException, URISyntaxException {

        String username = usernameTextfield.getText();

        FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
        Parent window = (Pane) fmxlLoader.load();
        controller = fmxlLoader.getController();
        this.scene = new Scene(window);

        //this gets the gateway from the load balancer
<<<<<<< HEAD
//        RestTemplate restTemplate = new RestTemplate();
//        String temp = restTemplate.getForObject("http://localhost:8081/getGateway", String.class);
=======
        //RestTemplate restTemplate = new RestTemplate();
        //String temp = restTemplate.getForObject("http://192.168.99.100:8081/getGateway", String.class);
>>>>>>> master
        Client client = new Client(new URI("ws://localhost:8080/"), username, controller);
        Thread x = new Thread(client);
        x.start();
    }

    public void changeScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage) usernameTextfield.getScene().getWindow();
            stage.setWidth(1040);
            stage.setHeight(620);
            stage.setScene(this.scene);
            stage.centerOnScreen();
            controller.setUsername(usernameTextfield.getText());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
