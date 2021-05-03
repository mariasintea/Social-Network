package socialnetwork.domain;

import java.util.List;

public class PageDTO{
    String name;
    String surname;
    String friendsList;
    String messageList;
    String requestList;

    public PageDTO(String name, String surname, String friendsList, String messageList, String requestList) {
        this.name = name;
        this.surname = surname;
        this.friendsList = friendsList;
        this.messageList = messageList;
        this.requestList = requestList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(String friendsList) {
        this.friendsList = friendsList;
    }

    public String getMessageList() {
        return messageList;
    }

    public void setMessageList(String messageList) {
        this.messageList = messageList;
    }

    public String getRequestList() {
        return requestList;
    }

    public void setRequestList(String requestList) {
        this.requestList = requestList;
    }
}
