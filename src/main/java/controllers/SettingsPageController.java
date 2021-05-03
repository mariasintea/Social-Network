package controllers;

import controllers.utils.MessageAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.io.File;

public class SettingsPageController {
    Service service;
    User mainUser;
    @FXML
    TextField nameField;
    @FXML
    TextField surnameField;
    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    TextField picturePath;

    /**
     * constructor of SettingsPageController
     */
    public SettingsPageController(){

    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainUser - controller's current user
     */
    public void setUp(Service service, User mainUser)
    {
        this.service = service;
        this.mainUser = mainUser;
        init();
    }

    /**
     * initiates TextFields with the right information about user
     * set editability for TextFields
     */
    void init()
    {
        nameField.setText(mainUser.getFirstName());
        surnameField.setText(mainUser.getLastName());
        usernameField.setText(mainUser.getUsername());
        passwordField.setText(mainUser.getPassword());
        passwordField.setEditable(false);
        nameField.setEditable(false);
        surnameField.setEditable(false);
        picturePath.setEditable(false);
    }

    /**
     * updates user's new info into the database
     * shows message if everything went good/bad
     * closes window when successful
     * @param event
     */
    public void handleSave(ActionEvent event){
        mainUser.setUsername(usernameField.getText());
        mainUser.setPicture(picturePath.getText());
        try {
            service.updateUser(mainUser);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS", "You just updated your profile!");
        }
        catch (ValidationException e)
        {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }

        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        thisStage.hide();
    }

    /**
     * opens a file chooser
     */
    public void handleSelectProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        picturePath.setText(selectedFile.getAbsolutePath());
    }
}
