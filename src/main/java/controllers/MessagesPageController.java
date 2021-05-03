package controllers;

import controllers.utils.MessageAlert;
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
import socialnetwork.domain.*;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesPageController implements Observer<MessageTaskChangeEvent> {
    Service service;
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
     * constructor of MessagesPageController
     */
    public MessagesPageController() {

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
        Iterable<MessageDTO> list = service.getAllMessages(mainUser.getId());
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

        int numberOfPages = service.getNumberOfPages(FilterType.MESSAGES);
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
     * opens AddMessage Window
     */
    public void handleAddMessage(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/addMessagePage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Message");
            stage.setScene(scene);

            AddMessagePageController controller = loader.getController();
            controller.setUp(service, mainUser, null, mainPage);

            stage.show();
        } catch (Exception e) {
        }
    }

    /**
     * opens Show Conversation Window for a selected message
     */
    public void handleShowConversation(){
        MessageDTO messageDTO = messagesTable.getSelectionModel().getSelectedItem();
        if(messageDTO != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/conversationsPage.fxml"));
            try {
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setTitle(messageDTO.getTo() + "'s and " + messageDTO.getFrom() + "'s conversation");
                stage.setScene(scene);

                ConversationsPageController controller = loader.getController();
                controller.setUp(service, mainUser, messageDTO, mainPage);

                stage.show();
            } catch (Exception e) {

            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing was selected!");
    }
}
