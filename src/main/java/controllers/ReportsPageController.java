package controllers;

import controllers.utils.MessageAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import socialnetwork.domain.FriendDTO;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.User;
import socialnetwork.service.Service;

import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.element.Cell;

public class ReportsPageController {
    Service service;
    User mainUser;
    @FXML
    ComboBox<String> usersFriends;
    @FXML
    DatePicker startTime;
    @FXML
    DatePicker endTime;

    /**
     * constructor of ReportsPageController
     */
    public ReportsPageController() {
    }

    /**
     * makes the set up for controller
     * @param service - current service
     * @param mainUser - current user
     */
    public void setUp(Service service, User mainUser) {
        this.service = service;
        this.mainUser = mainUser;
        init();
    }

    /**
     * initialises the ComboBox with user's friends
     */
    void init()
    {
        Iterable<User> list = service.getAllFriends(mainUser.getId());
        List<String> friendsList = StreamSupport.stream(list.spliterator(), false)
                .filter(x->x.getId() != mainUser.getId())
                .map(x->x.getUsername())
                .collect(Collectors.toList());

        ObservableList obList = FXCollections.observableList(friendsList);
        usersFriends.getItems().clear();
        usersFriends.setItems(obList);
    }

    /**
     * creates a pdf containing user's Activity Report between selected dates
     * shows message when task completed
     */
    public void handleActivityReport(){
        if(startTime.getValue() == null || endTime.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Period was not selected!");
            return;
        }

        String startDate = startTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = endTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        PdfDocument pdf = null;
        try {
            pdf = new PdfDocument(new PdfWriter("data/activitiesReport.pdf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Document document = new Document(pdf);
        String line = mainUser.getUsername() + "'s activity";
        document.add(new Paragraph(line));
        document.add(new Paragraph("Messages"));

        List<MessageDTO> mesaje = service.getMessagesFromPeriod(mainUser.getId(), startDate, endDate);

        float[] columnWidths = {10, 20, 15, 10};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        Cell[] headerFooter = new Cell[]{
                new Cell().add(new Paragraph("From")),
                new Cell().add(new Paragraph("To")),
                new Cell().add(new Paragraph("Message")),
                new Cell().add(new Paragraph("Date"))
        };
        for (Cell hfCell : headerFooter)
            table.addHeaderCell(hfCell);

        for (MessageDTO messageDTO: mesaje) {
            table.addCell(new Cell().add(new Paragraph(messageDTO.getFrom())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getTo())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getMessage())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getDate())));
        }
        document.add(table);

        document.add(new Paragraph("Friends"));
        List<FriendDTO> friends = service.getFriendsFromPeriod(mainUser.getId(), startDate, endDate);

        float[] columnWidths2 = {10, 10};
        Table table2 = new Table(UnitValue.createPercentArray(columnWidths2));
        headerFooter = new Cell[]{
                new Cell().add(new Paragraph("Username")),
                new Cell().add(new Paragraph("Date"))
        };

        for (Cell hfCell : headerFooter)
            table2.addHeaderCell(hfCell);

        for (FriendDTO friendDTO: friends) {
            table2.addCell(new Cell().add(new Paragraph(friendDTO.getUsername())));
            table2.addCell(new Cell().add(new Paragraph(friendDTO.getDate())));
        }
        document.add(table2);

        document.close();

        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "PDF just got created.");
    }

    /**
     * creates a pdf containing user's Messages Report between selected dates and friend
     * shows message when task completed
     */
    public void handleMessagesReport(){
        if(startTime.getValue() == null || endTime.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Period was not selected!");
            return;
        }
        if(usersFriends.getValue() == null)
        {
            MessageAlert.showErrorMessage(null, "Friend was not selected!");
            return;
        }
        String startDate = startTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = endTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String friend = usersFriends.getValue();

        PdfDocument pdf = null;
        try {
            pdf = new PdfDocument(new PdfWriter("data/messagesReport.pdf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Document document = new Document(pdf);
        String line = mainUser.getUsername() + "'s messages from " + friend;
        document.add(new Paragraph(line));

        List<MessageDTO> mesaje = service.getMessagesFromFriendAndPeriod(mainUser.getId(), service.searchUser(friend).getId(), startDate, endDate);

        float[] columnWidths = {10, 20, 15, 10};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        Cell[] headerFooter = new Cell[]{
                new Cell().add(new Paragraph("From")),
                new Cell().add(new Paragraph("To")),
                new Cell().add(new Paragraph("Message")),
                new Cell().add(new Paragraph("Date"))
        };

        for (Cell hfCell : headerFooter)
               table.addHeaderCell(hfCell);

        for (MessageDTO messageDTO: mesaje) {
            table.addCell(new Cell().add(new Paragraph(messageDTO.getFrom())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getTo())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getMessage())));
            table.addCell(new Cell().add(new Paragraph(messageDTO.getDate())));
        }


        document.add(table);
        document.close();

        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "SUCCESS!", "PDF just got created.");
    }
}
