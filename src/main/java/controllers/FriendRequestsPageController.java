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
import socialnetwork.domain.*;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsPageController implements Observer<MessageTaskChangeEvent> {
    User mainUser;
    Page mainPage;
    Service service;
    ObservableList<RequestDTO> modelReceived = FXCollections.observableArrayList();
    ObservableList<RequestDTO> modelSent = FXCollections.observableArrayList();
    @FXML
    TableView<RequestDTO> receivedFriendRequests;
    @FXML
    TableColumn<RequestDTO, String> dateColumnReceived;
    @FXML
    TableColumn<RequestDTO, String> usernameColumnReceived;
    @FXML
    TableView<RequestDTO> sentFriendRequests;
    @FXML
    TableColumn<RequestDTO, String> dateColumnSent;
    @FXML
    TableColumn<RequestDTO, String> usernameColumnSent;
    @FXML
    Pagination paginationReceived;
    @FXML
    Pagination paginationSent;

    /**
     * constructor of FriendRequestsPageController
     */
    public FriendRequestsPageController() {

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
        usernameColumnReceived.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("username"));
        dateColumnReceived.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("date"));
        receivedFriendRequests.setItems(modelReceived);
        usernameColumnSent.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("username"));
        dateColumnSent.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("date"));
        sentFriendRequests.setItems(modelSent);
    }

    /**
     * loads the elements from given page into the received requests TableView
     * @param page - given page
     */
    void loadTableReceived(Integer page)
    {
        service.setPage(page);
        Iterable<RequestDTO> list = service.getAllReceivedFriendRequests(mainUser.getId());
        List<RequestDTO> receivedRequestDTOList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        modelReceived.setAll(receivedRequestDTOList);
    }

    /**
     * loads the elements from given page into the sent requests TableView
     * @param page - given page
     */
    void loadTableSent(Integer page)
    {
        service.setPage(page);
        Iterable<RequestDTO> list = service.getAllSentFriendRequests(mainUser.getId());
        List<RequestDTO> sentRequestDTOList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        modelSent.setAll(sentRequestDTOList);
    }

    /**
     * initiates the received and sent requests TableViews with first page elements and
     * initiates pagination factory and max shown value for both tables
     */
    void init()
    {
        loadTableReceived(1);

        int numberOfPagesReceived = service.getNumberOfPages(FilterType.RECEIVED_REQUESTS);
        paginationReceived.setMaxPageIndicatorCount(numberOfPagesReceived);
        paginationReceived.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPagesReceived) {
                    return null;
                } else {
                    loadTableReceived(pageIndex);
                    return receivedFriendRequests;
                }
            }
        });

        loadTableSent(1);

        int numberOfPagesSent = service.getNumberOfPages(FilterType.SENT_REQUESTS);
        paginationSent.setMaxPageIndicatorCount(numberOfPagesSent);
        paginationSent.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPagesSent) {
                    return null;
                } else {
                    loadTableSent(pageIndex);
                    return sentFriendRequests;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }

    /**
     * updates a friend request's status to approved
     * shows message if everything went good/bad
     */
    public void handleAcceptFriendRequest(){
        RequestDTO request = receivedFriendRequests.getSelectionModel().getSelectedItem();
        if(request != null)
        {
            FriendshipRequest friendshipRequest = new FriendshipRequest();
            User user = service.searchUser(request.getUsername());
            friendshipRequest.setId(new Tuple<>(user.getId(), mainUser.getId()));
            friendshipRequest.setStatus("approved");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            friendshipRequest.setDate(dtf.format(LocalDateTime.now()));
            try
            {
                service.addFriendRequestApproved(friendshipRequest);
                List<Tuple<Long, Long>> friendsList = mainPage.getFriendsList();
                Page page = service.searchPage(user.getFirstName(), user.getLastName());
                friendsList.add(new Tuple<>(mainUser.getId(), user.getId()));
                mainPage.setFriendsList(friendsList);
                service.updatePage(mainPage);

                friendsList.clear();
                friendsList = page.getFriendsList();
                friendsList.add(new Tuple<>(mainUser.getId(), user.getId()));
                page.setFriendsList(friendsList);
                service.updatePage(page);
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Friend request approved!");
            }
            catch (IllegalArgumentException ex)
            {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing is selected!");
    }

    /**
     * updates a friend request's status to rejected
     * shows message if everything went good/bad
     */
    public void handleDeclineFriendRequest(){
        RequestDTO request = receivedFriendRequests.getSelectionModel().getSelectedItem();
        if(request != null)
        {
            FriendshipRequest friendshipRequest = new FriendshipRequest();
            User user = service.searchUser(request.getUsername());
            friendshipRequest.setId(new Tuple<>(user.getId(), mainUser.getId()));
            friendshipRequest.setStatus("rejected");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            friendshipRequest.setDate(dtf.format(LocalDateTime.now()));
            try{
                service.addFriendRequestRejected(friendshipRequest);
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Friend request rejected!");
            }
                catch (IllegalArgumentException ex)
            {
                MessageAlert.showErrorMessage(null, ex.getMessage());
            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing is selected!");
    }

    /**
     * deletes a friend request from database
     * shows message if everything went good/bad
     */
    public void handleDeleteFriendRequest(){
        RequestDTO request = sentFriendRequests.getSelectionModel().getSelectedItem();
        if(request != null)
        {
            User user = service.searchUser(request.getUsername());
            Page page = service.searchPage(user.getFirstName(), user.getLastName());
            service.deleteRequest(new Tuple<>(mainUser.getId(), user.getId()));

            List<Tuple<Long, Long>> requestsList = mainPage.getRequestList();
            requestsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
            mainPage.setRequestList(requestsList);
            service.updatePage(mainPage);
            requestsList = page.getRequestList();
            requestsList.remove(new Tuple<>(mainUser.getId(), user.getId()));
            page.setRequestList(requestsList);
            service.updatePage(page);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS", "Request deleted!");
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing is selected!");
    }

    /**
     * opens the ShowHistory Window
     */
    public void handleShowHistory() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/showRequestHistoryPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Request History");
            stage.setScene(scene);

            ShowRequestsHistoryPageController controller = loader.getController();
            controller.setUp(service, mainUser);

            stage.show();
        } catch (Exception e) {

        }
    }
}
