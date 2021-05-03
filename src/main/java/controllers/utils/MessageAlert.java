package controllers.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {

    /**
     * creates an alert message
     * @param owner - the owner of the alert object
     * @param type - the type of the alert object
     * @param header - the header of the alert object
     * @param text - the text of the alert object
     */
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    /**
     * creates an error alert message
     * @param owner - the owner of the alert object
     * @param text - the text of the alert object
     */
    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error Message");
        message.setContentText(text);
        message.showAndWait();
    }
}

