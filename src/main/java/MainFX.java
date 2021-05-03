
import controllers.StartPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
        import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
        import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        try {
            initView(stage);
        }
        catch (Exception e)
        {

        }
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader messageLoader = new FXMLLoader();
        messageLoader.setLocation(getClass().getResource("/startPage.fxml"));
        AnchorPane startPageLayout = messageLoader.load();
        primaryStage.setScene(new Scene(startPageLayout));

        StartPageController controller = messageLoader.getController();
    }

}