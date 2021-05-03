package controllers;

import controllers.utils.MessageAlert;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.Message;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddMessagePageController{
    Service service;
    User mainUser;
    Page mainPage;
    MessageDTO mainMessage;
    @FXML
    Pane toPanel;
    @FXML
    TextArea messageArea;
    List<Long> toList;
    @FXML
    Pagination pagination;

    /**
     * constructor of AddMessagePageController
     */
    public AddMessagePageController(){

    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainUser - controller's current user
     * @param messageDTO - controller's current message
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, User mainUser, MessageDTO messageDTO, Page mainPage) {
        this.service = service;
        this.mainUser = mainUser;
        this.mainPage = mainPage;
        this.mainMessage = messageDTO;
        toList = new ArrayList<Long>();
        init();
    }

    /**
     * loads the elements from given page into the Pane
     * creates event handler for all added CheckBoxes
     * @param page - given page
     */
    void loadPanel(Integer page)
    {
        toPanel.getChildren().clear();

        service.setPage(page);
        Iterable<User> list = service.getAllUsersFromMessage(mainMessage, mainUser);
        List<User> usersList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());

        VBox layout = new VBox(5);
        for(User u: usersList) {
            CheckBox choice = new CheckBox(u.getUsername());
            layout.getChildren().add(choice);

            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {

                public void handle(ActionEvent e) {
                    if (choice.isSelected())
                        toList.add(service.searchUser(choice.getText()).getId());
                    if (!choice.isSelected())
                        toList.remove(service.searchUser(choice.getText()).getId());
                }
            };
            choice.setOnAction(event);
        }

        toPanel.getChildren().add(layout);
    }

    /**
     * initiates the Pane with first page elements and
     * initiates pagination factory and max shown value
     */
    public void init() {
        loadPanel(1);

        int numberOfPages = service.getNumberOfPages(FilterType.USERS_MESSAGE);
        pagination.setMaxPageIndicatorCount(numberOfPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPages) {
                    return null;
                } else {
                    loadPanel(pageIndex);
                    return toPanel;
                }
            }
        });
    }

    /**
     * creates a message and adds it to the database
     * informs if everything went good/bad
     * closes the page after success
     * @param event
     */
    public void handleSendMessage(ActionEvent event){
        if(toList.size() == 0)
            MessageAlert.showErrorMessage(null, "Nothing was selected!");
        else {
            Message message = new Message(mainUser.getId(), toList, messageArea.getText(), null);
            if (mainMessage != null) {
                service.addReply(message, mainMessage.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "You replied successfully in this conversation!");
            } else {
                service.addMessage(message);
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "You started a new conversation successfully!");
            }
            List<Long> messagesList = mainPage.getMessageList();
            messagesList.add(service.searchMessageID(message));
            mainPage.setMessageList(messagesList);
            service.updatePage(mainPage);

            for (Long id: toList)
            {
                User user = service.searchUserByID(id);
                Page page = service.searchPage(user.getFirstName(), user.getLastName());
                messagesList = page.getMessageList();
                messagesList.add(service.searchMessageID(message));
                page.setMessageList(messagesList);
                service.updatePage(page);
            }

            Node node = (Node) event.getSource();
            Stage thisStage = (Stage) node.getScene().getWindow();
            thisStage.hide();
        }
    }
}
