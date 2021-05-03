package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.domain.Event;
import socialnetwork.domain.Page;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.database.paginated.*;
import socialnetwork.repository.paging.*;
import socialnetwork.service.Service;

public class StartPageController {
    Service service;

    /**
     * constructor for StartPageController
     * creates all repositories and the service
     */
    public StartPageController() {
        /*Repository<Long, User> userRepository;
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
        Repository<Tuple<Long, Long>, FriendshipRequest> friendRequestsRepository;
        Repository<Long, Message> messagesRepository;
        Repository<Long, Page> pagesRepository;
        Repository<Long, Event> eventsRepository;*/

        PaginatedRepository<Long, User> userRepository;
        PaginatedRepository<Tuple<Long, Long>, Friendship> friendshipRepository;
        PaginatedRepository<Tuple<Long, Long>, FriendshipRequest> friendRequestsRepository;
        PaginatedRepository<Long, Message> messagesRepository;
        PaginatedRepository<Long, Page> pagesRepository;
        PaginatedRepository<Long, Event> eventsRepository;

        /*String userFileName="data/users.csv";
        String friendshipFileName="data/friendships.csv";
        String friendRequestFileName="data/requests.csv";
        String messageFileName="data/messages.csv";
        userRepository = new UserFile(userFileName, new UserValidator());
        friendshipRepository = new FriendshipFile(friendshipFileName, new FriendshipValidator());
        friendRequestsRepository = new RequestFile(friendRequestFileName, new RequestValidator());
        messagesRepository = new MessageFile(messageFileName, new MessageValidator());*/
        /*userRepository = new UserDatabase("users", new UserValidator());
        friendshipRepository = new FriendshipDatabase("friendships", new FriendshipValidator());
        friendRequestsRepository = new FriendRequestDatabase("requests", new RequestValidator());
        messagesRepository = new MessageDatabase("messages", new MessageValidator());
        pagesRepository = new PageDatabase("pages", new PageValidator());
        eventsRepository = new EventDatabase("events", new EventValidator());*/

        userRepository = new PaginatedUserDatabase("users", new UserValidator());
        friendshipRepository = new PaginatedFriendshipDatabase("friendships", new FriendshipValidator());
        friendRequestsRepository = new PaginatedFriendRequestDatabase("requests", new RequestValidator());
        messagesRepository = new PaginatedMessageDatabase("messages", new MessageValidator());
        pagesRepository = new PaginatedPageDatabase("pages", new PageValidator());
        eventsRepository = new PaginatedEventDatabase("events", new EventValidator());

        service = new Service(userRepository, friendshipRepository, friendRequestsRepository, messagesRepository, pagesRepository, eventsRepository);
    }

    /**
     * opens LogIn Window
     */
    public void handleLogInPage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/logInPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Log In");
            stage.setScene(scene);

            LogInPageController controller = loader.getController();
            controller.setService(service);

            stage.show();
        }
        catch (Exception e)
        {

        }
    }

    /**
     * opens SignUp Window
     */
    public void handleSignUpPage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/signUpPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Sign Up");
            stage.setScene(scene);

            SignUpPageController controller = loader.getController();
            controller.setService(service);

            stage.show();
        }
        catch (Exception e)
        {

        }
    }
}
