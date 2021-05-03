package controllers;

import controllers.utils.MessageAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import socialnetwork.domain.Event;
import socialnetwork.domain.EventDTO;
import socialnetwork.domain.Page;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.service.TimerService;
import controllers.utils.CustomTimerTask;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SubscribeToEventPageController implements Observer<MessageTaskChangeEvent> {
    Page mainPage;
    User mainUser;
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
     * constructor of EventPageController
     */
    public SubscribeToEventPageController() {
    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param timerService - controller's current timer service
     * @param mainUser - controller's current user
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, TimerService timerService, Page mainPage, User mainUser)
    {
        this.service = service;
        this.timerService = timerService;
        this.mainPage = mainPage;
        this.mainUser = mainUser;
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
        Iterable<EventDTO> list = service.getAllEvents(mainPage);
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

        int numberOfPages = service.getNumberOfPages(FilterType.EVENTS);
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
     * adds the current user to the subscribers list of the selected event
     * creates two timers, if necessary:
     * - one for the "Less than five minutes" notification
     * - one for the "Less than a minute" notification
     * @param event
     */
    public void handleSubscribe(ActionEvent event){
        EventDTO selectedEvent = tableEvents.getSelectionModel().getSelectedItem();
        if(selectedEvent != null)
        {
            try
            {
                Event updatedEvent = service.searchEvent(selectedEvent.getID());

                long delay = getDelay(updatedEvent);
                if (delay < -5)
                    MessageAlert.showMessage(null, Alert.AlertType.WARNING, "WARNING", "Event already happened!");
                else {
                    List<Long> subscribersList = updatedEvent.getSubscribers();
                    if(!subscribersList.contains(mainUser.getId()))
                        subscribersList.add(mainUser.getId());
                    else
                    {
                        MessageAlert.showErrorMessage(null, "You already subscribed for this event!");
                        return;
                    }
                    updatedEvent.setSubscribers(subscribersList);
                    service.updateEvent(updatedEvent);
                    MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "Successful subscription to event!");

                    if (delay < 0)
                        MessageAlert.showMessage(null, Alert.AlertType.WARNING, "WARNING", "Event taking place in less than five minutes!");
                    else {
                        Timer timer = new Timer("Timer 5 more minutes");
                        timer.schedule(new CustomTimerTask("EVENT STARTING SOON", "Less than five minutes"), TimeUnit.MINUTES.toMillis(delay));
                        timerService.addTimer5Min(mainUser.getId(), timer);
                    }

                    Timer timer = new Timer("Timer");
                    delay += 5;
                    timer.schedule(new CustomTimerTask("EVENT HAPPENING NOW", "Less than a minute"), TimeUnit.MINUTES.toMillis(delay));
                    timerService.addTimer(mainUser.getId(), timer);

                    Node node = (Node) event.getSource();
                    Stage thisStage = (Stage) node.getScene().getWindow();
                    thisStage.hide();
                }
            }
            catch (Exception e)
            {

            }
        }
        else
            MessageAlert.showErrorMessage(null, "Nothing was selected!");
    }

    /**
     * computes the difference between the event's date and current date
     * @param event - selected event
     * @return difference between the event's date and current date in minutes
     */
    private long getDelay(Event event)
    {
        String eventDate = event.getDate().substring(0, 10);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String presentDate = df.format(new Date());
        String eventTime = event.getDate().substring(11, 16) + ":00";
        DateFormat dft = new SimpleDateFormat("HH:mm:ss");
        String presentTime = dft.format(new Date());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(eventDate);
            Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(presentDate);
            long diff = TimeUnit.MILLISECONDS.toMinutes(date.getTime() - currentDate.getTime());

            Date time = new SimpleDateFormat("HH:mm:ss").parse(eventTime);
            Date currentTime = new SimpleDateFormat("HH:mm:ss").parse(presentTime);
            diff += TimeUnit.MILLISECONDS.toMinutes(time.getTime() - currentTime.getTime());

            diff -= 5;
            return diff;
        }
        catch (Exception e){

        }
        return  0L;
    }
}
