package controllers;

import controllers.utils.MessageAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Event;
import socialnetwork.domain.Page;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddEventPageController {
    Page mainPage;
    Service service;
    @FXML
    DatePicker eventDate;
    @FXML
    TextField eventName;
    @FXML
    ComboBox<Integer> eventHour;
    @FXML
    ComboBox<Integer> eventMinute;

    /**
     * constructor of AddEventPageController
     */
    public AddEventPageController() {
    }

    /**
     * makes the set up for the controller
     * @param service - controller's current service
     * @param mainPage - controller's current page
     */
    public void setUp(Service service, Page mainPage)
    {
        this.service = service;
        this.mainPage = mainPage;
        init();
    }

    /**
     * initiates the ComboBoxes for hour and minute
     */
    private void init()
    {
        List<Integer> hourList = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            hourList.add(i);
        ObservableList hourObList = FXCollections.observableList(hourList);
        eventHour.getItems().clear();
        eventHour.setItems(hourObList);

        List<Integer> minList = new ArrayList<>();
        for (int i = 0; i < 60; i++)
            minList.add(i);
        ObservableList minObList = FXCollections.observableList(minList);
        eventMinute.getItems().clear();
        eventMinute.setItems(minObList);
    }

    /**
     * creates an event and adds it to the database
     * shows message if everything went good/bad
     * closes the page after success
     * @param event
     */
    public void handleAddEvent(ActionEvent event)
    {
        if(eventDate.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Date was not selected!");
            return;
        }
        if(eventHour.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Hour was not selected!");
            return;
        }
        if(eventMinute.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Minute was not selected!");
            return;
        }
        DecimalFormat decimalFormat = new DecimalFormat("00");
        String date = eventDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + decimalFormat.format(eventHour.getValue()) + ":" + decimalFormat.format(eventMinute.getValue());
        try
        {
            service.addEvent(new Event(mainPage.getId(), eventName.getText(), date, new ArrayList<Long>()));
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "Event added successfully!");

            Node node = (Node) event.getSource();
            Stage thisStage = (Stage) node.getScene().getWindow();
            thisStage.hide();
        }
        catch (ValidationException e)
        {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
