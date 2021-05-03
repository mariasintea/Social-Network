package controllers;

import controllers.utils.MessageAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import javafx.scene.control.TextField;

import java.util.ArrayList;

public class SignUpPageController {
    Service service;
    @FXML
    TextField nameField;
    @FXML
    TextField surnameField;
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repasswordField;

    /**
     * constructor for SignUpPageController
     */
    public SignUpPageController() {
    }

    /**
     * makes set up for controller
     * @param service - current service
     */
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * creates a user with the given information
     * adds the new user to the database
     * shows message if everything went good/bad
     * closes window when successful
     * @param event
     */
    public void handleSignUp(ActionEvent event){
        String name = nameField.getText();
        String surname = surnameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String repassword = repasswordField.getText();
        String picture = "C:\\Users\\Maria\\Documents\\SocialNetwork\\social-network\\src\\main\\resources\\images\\profile.jpg";
        User user = new User(name, surname, username, password, picture);
        if(password.equals(repassword))
        {
            try {
                service.addUser(user);
                service.addPage(new Page(user.getFirstName(), user.getLastName(), new ArrayList<Tuple<Long, Long>>(), new ArrayList<Long>(), new ArrayList<Tuple<Long, Long>>()));
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "User created successfully!");

                Node node = (Node) event.getSource();
                Stage thisStage = (Stage) node.getScene().getWindow();
                thisStage.hide();
            }
            catch (ValidationException exception)
            {
                MessageAlert.showErrorMessage(null, exception.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Passwords don't match!");
    }
}
