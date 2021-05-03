package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import socialnetwork.domain.RequestDTO;
import socialnetwork.domain.User;
import socialnetwork.service.Service;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ShowRequestsHistoryPageController implements Observer<MessageTaskChangeEvent> {

    Service service;
    User mainUser;
    ObservableList<RequestDTO> model = FXCollections.observableArrayList();
    @FXML
    TableView<RequestDTO> requestsTableView;
    @FXML
    TableColumn<RequestDTO, String> statusColumn;
    @FXML
    TableColumn<RequestDTO, String> dateColumn;
    @FXML
    TableColumn<RequestDTO, String> usernameColumn;
    @FXML
    Pagination pagination;

    /**
     * makes set up for controller
     * @param service - current service
     * @param mainUser - current user
     */
    public void setUp(Service service, User mainUser) {
        this.service = service;
        this.mainUser = mainUser;
        service.addObserver(this);
        init();
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("username"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<RequestDTO, String>("date"));
        requestsTableView.setItems(model);
    }

    /**
     * loads the elements from given page into the TableView
     * @param page - given page
     */
    void loadTable(Integer page)
    {
        service.setPage(page);
        Iterable<RequestDTO> list = service.getAllFriendRequests(mainUser.getId());
        List<RequestDTO> requestDTOList = StreamSupport.stream(list.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(requestDTOList);
    }

    /**
     * initiates the TableView with first page elements and
     * initiates pagination factory and max shown value
     */
    void init()
    {
        loadTable(1);

        int numberOfPagesSent = service.getNumberOfPages(FilterType.REQUESTS);
        pagination.setMaxPageIndicatorCount(numberOfPagesSent);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= numberOfPagesSent) {
                    return null;
                } else {
                    loadTable(pageIndex);
                    return requestsTableView;
                }
            }
        });
    }

    @Override
    public void update(MessageTaskChangeEvent messageTaskChangeEvent) {
        init();
    }
}
