package controllers;

import controllers.utils.MessageAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.Page;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendsPageController implements Observer<MessageTaskChangeEvent> {
    Service service;
    User mainUser;
    Page mainPage;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> friendsTable;
    @FXML
    TableColumn<User, String> nameColumn;
    @FXML
    TableColumn<User, String> surnameColumn;
    @FXML
    TableColumn<User, String> usernameColumn;
    @FXML
    Pagination pagination;

    /**
     * constructor of FriendsPageController
     */
    public FriendsPageController() {

    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainUser - controller's current user
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, User mainUser, Page mainPage) {
        this.service = service;
        this.mainUser = mainUser;
        this.mainPage = mainPage;
        service.addObserver(this);
        init();
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        friendsTable.setItems(model);
    }

    /**
     * loads the elements from given page into the TableView
     * @param page - given page
     */
    void loadTable(Integer page)
    {
        service.setPage(page);
        Iterable<User> list = service.getAllFriends(mainUser.getId());
        List<User> friendsList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendsList);
    }

    /**
     * initiates the TableView with first page elements and
     * initiates pagination factory and max shown value
     */
    void init()
    {
        loadTable(1);

        int numberOfPages = service.getNumberOfPages(FilterType.FRIENDS);
        pagination.setMaxPageIndicatorCount(numberOfPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPages) {
                    return null;
                } else {
                    loadTable(pageIndex);
                    return friendsTable;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }

    /**
     * opens the AddFriend Window
     */
    public void handleAddFriend(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/addFriendPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Friend Requests");
            stage.setScene(scene);

            AddFriendPageController controller = loader.getController();
            controller.setUp(service, mainUser, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * deletes selected friend of mainUser from database
     * shows message if everything went good/bad
     */
    public void handleDeleteFriend(){
        User user = friendsTable.getSelectionModel().getSelectedItem();
        if(user != null)
        {
            try {
                service.removeFriendship(mainUser.getId(), user.getId());
                service.deleteRequest(new Tuple<>(mainUser.getId(), user.getId()));
                Page page = service.searchPage(user.getFirstName(), user.getLastName());

                List<Tuple<Long, Long>> friendsList = mainPage.getFriendsList();
                if(friendsList.contains(new Tuple<>(mainUser.getId(), user.getId())))
                    friendsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
                else
                    friendsList.remove(new Tuple<>(user.getId(), mainUser.getId()));
                mainPage.setFriendsList(friendsList);
                service.updatePage(mainPage);
                friendsList = page.getFriendsList();
                if(friendsList.contains(new Tuple<>(mainUser.getId(), user.getId())))
                    friendsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
                else
                    friendsList.remove(new Tuple<>(user.getId(), mainUser.getId()));
                page.setFriendsList(friendsList);
                service.updatePage(page);

                List<Tuple<Long, Long>> requestsList = mainPage.getRequestList();
                if(requestsList.contains(new Tuple<>(mainUser.getId(), user.getId())))
                    requestsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
                else
                    requestsList.remove(new Tuple<>(user.getId(), mainUser.getId()));
                mainPage.setRequestList(requestsList);
                service.updatePage(mainPage);
                requestsList = page.getRequestList();
                if(requestsList.contains(new Tuple<>(mainUser.getId(), user.getId())))
                    requestsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
                else
                    requestsList.remove(new Tuple<>(user.getId(), mainUser.getId()));
                page.setRequestList(requestsList);
                service.updatePage(page);

                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS", "Friendship deleted!");
            }
            catch(IllegalArgumentException ex)
            {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing is selected!");
    }
}
