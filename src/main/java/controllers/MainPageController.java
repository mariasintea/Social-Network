package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.service.TimerService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static java.lang.Math.min;

public class MainPageController {
    Service service;
    TimerService serviceTimer;
    User mainUser;
    Page mainPage;
    @FXML
    Label label;
    @FXML
    ImageView profilePicture;

    /**
     * constructor of MainPageController
     * initializes the timer service
     */
    public MainPageController() {
        serviceTimer = new TimerService();
    }

    /**
     * makes the set up for controller
     * @param service - current service
     * @param mainUser - current user
     * @param mainPage - current page
     */
    public void setUp(Service service, User mainUser, Page mainPage) {
        this.service = service;
        this.mainUser = mainUser;
        this.mainPage = mainPage;
        init();
    }

    /**
     * sets the label with user's name and the ImageView with user's profile picture
     */
    private void init()
    {
        label.setText(mainUser.getFirstName() + " " + mainUser.getLastName());
        try {
            Image picture = new Image(new FileInputStream(mainUser.getPicture()));
            profilePicture.setImage(picture);
            centerImage(picture);
        }
        catch (FileNotFoundException e)
        {

        }
    }

    /**
     * centers img in the ImageView
     * @param img - given image
     */
    private void centerImage(Image img) {
        double width = 0;
        double height = 0;

        double ratioX = profilePicture.getFitWidth() / img.getWidth();
        double ratioY = profilePicture.getFitHeight() / img.getHeight();

        double reduceCoefficient = min(ratioX, ratioY);

        width = img.getWidth() * reduceCoefficient;
        height = img.getHeight() * reduceCoefficient;

        profilePicture.setX((profilePicture.getFitWidth() - width) / 2);
        profilePicture.setY((profilePicture.getFitHeight() - height) / 2);
    }

    /**
     * opens Friend Request Window
     */
    public void handleShowFriendRequests(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/friendRequestsPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(mainUser.getFirstName() + "'s friend requests");
            stage.setScene(scene);

            FriendRequestsPageController controller = loader.getController();
            controller.setUp(service, mainUser, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens Friends Window
     */
    public void handleShowFriends(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/friendsPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(mainUser.getFirstName() + "'s friends");
            stage.setScene(scene);

            FriendsPageController controller = loader.getController();
            controller.setUp(service, mainUser, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens Messages Window
     */
    public void handleConversations(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/messagesPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(mainUser.getFirstName() + "'s messages");
            stage.setScene(scene);

            MessagesPageController controller = loader.getController();
            controller.setUp(service, mainUser, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens Reports Window
     */
    public void handleReports(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/reportsPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(mainUser.getFirstName() + "'s reports");
            stage.setScene(scene);

            ReportsPageController controller = loader.getController();
            controller.setUp(service, mainUser);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens Settings Window
     */
    public void handleSettings(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/settingsPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);

            SettingsPageController controller = loader.getController();
            controller.setUp(service, mainUser);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens Events Window
     */
    public void handleEvents(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/eventsPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle(mainUser.getFirstName() + "'s events");
            stage.setScene(scene);

            EventsPageController controller = loader.getController();
            controller.setUp(service, serviceTimer, mainUser, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }
}
