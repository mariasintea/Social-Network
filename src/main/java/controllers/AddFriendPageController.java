package controllers;

import controllers.utils.MessageAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.*;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendPageController implements Observer<MessageTaskChangeEvent> {
    Service service;
    User mainUser;
    Page mainPage;
    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    TableView<User> tableUsers;
    @FXML
    TableColumn<User, String> nameColumn;
    @FXML
    TableColumn<User, String> surnameColumn;
    @FXML
    TableColumn<User, String> usernameColumn;
    @FXML
    Pagination pagination;

    /**
     * constructor of AddFriendPageController
     */
    public AddFriendPageController() {
    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainPage - controller's current page
     * @param mainUser - controller's current user
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
        tableUsers.setItems(model);
    }

    /**
     * loads the elements from given page into the TableView
     * @param page - given page
     */
    void loadTable(Integer page)
    {
        service.setPage(page);
        Iterable<User> list = service.getAllUsers(mainUser.getId());
        List<User> usersList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(usersList);
    }

    /**
     * initiates the TableView with first page elements and
     * initiates pagination factory and max shown value
     */
    void init()
    {
        loadTable(1);

        int numberOfPages = service.getNumberOfPages(FilterType.USERS);
        pagination.setMaxPageIndicatorCount(numberOfPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPages) {
                    return null;
                } else {
                    loadTable(pageIndex);
                    return tableUsers;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }

    /**
     * creates a friend request and adds it to the database
     * shows message if everything went good/bad
     * closes the page after success
     * @param event
     */
    public void handleSendFriendRequest(ActionEvent event){
        User user = tableUsers.getSelectionModel().getSelectedItem();
        if(user != null)
        {
            FriendshipRequest request = new FriendshipRequest();
            request.setId(new Tuple<Long, Long>(mainUser.getId(), user.getId()));
            try
            {
                FriendshipRequest newFriendRequest = service.addFriendRequestPending(request);
                if(newFriendRequest != null)
                    MessageAlert.showErrorMessage(null, "Friend request already exists!");
                else {
                    MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS", "Friend Request sent!");
                    List<Tuple<Long, Long>> requestsList = mainPage.getRequestList();
                    Page page = service.searchPage(user.getFirstName(), user.getLastName());
                    requestsList.add(new Tuple<>(mainUser.getId(), user.getId()));
                    mainPage.setRequestList(requestsList);
                    service.updatePage(mainPage);
                    requestsList = page.getRequestList();
                    requestsList.add(new Tuple<>(mainUser.getId(), user.getId()));
                    page.setRequestList(requestsList);
                    service.updatePage(page);

                    Node node = (Node) event.getSource();
                    Stage thisStage = (Stage) node.getScene().getWindow();
                    thisStage.hide();
                }
            }
            catch (IllegalArgumentException ex)
            {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing was selected!");
    }
}
