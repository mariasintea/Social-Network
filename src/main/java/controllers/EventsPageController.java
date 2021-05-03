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
import socialnetwork.domain.Event;
import socialnetwork.domain.EventDTO;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.service.TimerService;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventsPageController implements Observer<MessageTaskChangeEvent> {
    User mainUser;
    Page mainPage;
    Service service;
    TimerService timerService;
    ObservableList<EventDTO> model = FXCollections.observableArrayList();
    @FXML
    TableView<EventDTO> tableEvents;
    @FXML
    TableColumn<EventDTO, String> organizerColumn;
    @FXML
    TableColumn<EventDTO, String> nameColumn;
    @FXML
    TableColumn<EventDTO, String> dateColumn;
    @FXML
    Pagination pagination;

    /**
     * constructor of EventsPageController
     */
    public EventsPageController() {
    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param timerService - controller's current timer service
     * @param mainUser - controller's current user
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, TimerService timerService, User mainUser, Page mainPage)
    {
        this.service = service;
        this.timerService = timerService;
        this.mainUser = mainUser;
        this.mainPage = mainPage;
        service.addObserver(this);
        init();
    }

    @FXML
    public void initialize() {
        organizerColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("organizer"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("date"));
        tableEvents.setItems(model);
    }

    /**
     * loads the elements from given page into the TableView
     * @param page - given page
     */
    void loadTable(Integer page)
    {
        service.setPage(page);
        Iterable<EventDTO> list = service.getAllSubscribedEvents(mainUser);
        List<EventDTO> eventsList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(eventsList);
    }

    /**
     * initiates the TableView with first page elements and
     * initiates pagination factory and max shown value
     */
    void init()
    {
        loadTable(1);

        int numberOfPages = service.getNumberOfPages(FilterType.SUBSCRIBED_EVENTS);
        pagination.setMaxPageIndicatorCount(numberOfPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPages) {
                    return null;
                } else {
                    loadTable(pageIndex);
                    return tableEvents;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }

    /**
     * eliminates user from subscribedEvents table
     * cancels the event timers if any
     * informs if everything went good/bad
     */
    public void handleUnsubscribe(){
        EventDTO event = tableEvents.getSelectionModel().getSelectedItem();
        if(event != null)
        {
            try
            {
                Event updatedEvent = service.searchEvent(event.getID());
                List<Long> subscribersList = updatedEvent.getSubscribers();
                subscribersList.remove(mainUser.getId());
                updatedEvent.setSubscribers(subscribersList);
                service.updateEvent(updatedEvent);
                Timer timer = timerService.getTimer5Min(mainUser.getId());
                if(timer != null)
                    timer.cancel();
                timer = timerService.getTimer(mainUser.getId());
                if(timer != null)
                    timer.cancel();
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "Successful unsubscription to event!");
            }
            catch (Exception e)
            {

            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing was selected!");
    }

    /**
     * opens the AddEvent Window
     */
    public void handleAddEvent(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/addEventPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Add Event");
            stage.setScene(scene);

            AddEventPageController controller = loader.getController();
            controller.setUp(service, mainPage);

            stage.show();
        } catch (Exception e) {

        }
    }

    /**
     * opens the SubscribeToEvent Window
     */
    public void handleSubscribeToEvent(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/subscribeToEventPage.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Subscribe To Event");
            stage.setScene(scene);

            SubscribeToEventPageController controller = loader.getController();
            controller.setUp(service, timerService, mainPage, mainUser);

            stage.show();
        } catch (Exception e) {

        }
    }
}
