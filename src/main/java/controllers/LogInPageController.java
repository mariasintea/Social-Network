package controllers;

import controllers.utils.MessageAlert;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

public class LogInPageController {
    Service service;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;

    /**
     * constructor of LogInPageController
     */
    public LogInPageController() {

    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * opens main Page if password corresponds to user
     * shows message if not
     */
    public void handleLogIn() {
        User user = service.searchUser(usernameField.getText());
        Page page = service.searchPage(user.getFirstName(), user.getLastName());
        if(user == null)
            MessageAlert.showErrorMessage(null, "No such user!");
        else
        if(service.checkPassword(usernameField.getText(), passwordField.getText())) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/mainPage.fxml"));
            try {
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setTitle(user.getFirstName() + "'s Page");
                stage.setScene(scene);

                MainPageController controller = loader.getController();
                controller.setUp(service, user, page);

                stage.show();
            } catch (Exception e) {

            }

            usernameField.setText("");
            passwordField.setText("");
        }
        else
        {
            MessageAlert.showErrorMessage(null, "Incorrect password!");
        }
    }
}
