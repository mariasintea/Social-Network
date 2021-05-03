package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConversationsPageController implements Observer<MessageTaskChangeEvent> {
    Service service;
    MessageDTO mainMessage;
    User mainUser;
    Page mainPage;
    ObservableList<MessageDTO> model = FXCollections.observableArrayList();
    @FXML
    TableView<MessageDTO> messagesTable;
    @FXML
    TableColumn<MessageDTO, String> fromColumn;
    @FXML
    TableColumn<MessageDTO, String> toColumn;
    @FXML
    TableColumn<MessageDTO, String> dateColumn;
    @FXML
    TableColumn<MessageDTO, String> messageColumn;
    @FXML
    Pagination pagination;

    /**
     * constructor of ConversationsPageController
     */
    public ConversationsPageController() {

    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainUser - controller's current user
     * @param mainMessage - controller's current message
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, User mainUser, MessageDTO mainMessage, Page mainPage) {
        this.service = service;
        this.mainUser = mainUser;
        this.mainPage = mainPage;
        this.mainMessage = mainMessage;
        service.addObserver(this);
        init();
    }


    @FXML
    public void initialize() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("to"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("date"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<MessageDTO, String>("message"));
        messagesTable.setItems(model);
    }

    /**
     * loads the elements from given page into the TableView
     * @param page - given page
     */
    void loadTable(Integer page)
    {
        service.setPage(page);
        Iterable<MessageDTO> list = service.getConversation(mainMessage.getId(), mainUser.getId());
        List<MessageDTO> messageList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageList);
    }

    /**
     * initiates the TableView with first page elements and
     * initiates pagination factory and max shown value
     */
    void init()
    {
        loadTable(1);

        int numberOfPages = service.getNumberOfPages(FilterType.CONVERSATIONS);
        pagination.setMaxPageIndicatorCount(numberOfPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPages) {
                    return null;
                } else {
                    loadTable(pageIndex);
                    return messagesTable;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }

    /**
     * opens the AddReply Window
     */
    public void handleAddReply(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/addMessagePage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Message");
            stage.setScene(scene);

            AddMessagePageController controller = loader.getController();
            controller.setUp(service, mainUser, mainMessage, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }
}
