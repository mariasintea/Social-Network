package controllers.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.TimerTask;

public class CustomTimerTask extends TimerTask {
    String header, content;

    /**
     * creates a custom timer task
     * @param header - the header of the message
     * @param content - the message that will be shown
     */
    public CustomTimerTask(String header, String content) {
        this.header = header;
        this.content = content;
    }

    /**
     * creates an alert message with the given header and content
     */
    @Override
    public void run() {
        Platform.runLater(() -> {
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            message.setHeaderText(header);
            message.setContentText(content);
            message.showAndWait();
        });
    }
}
