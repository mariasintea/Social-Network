package socialnetwork.domain;

import java.util.List;

public class Page extends Entity<Long>{
    String name;
    String surname;
    List<Tuple<Long, Long>> friendsList;
    List<Long> messageList;
    List<Tuple<Long, Long>> requestList;

    public Page(String name, String surname, List<Tuple<Long, Long>> friendsList, List<Long> messageList, List<Tuple<Long, Long>> requestList) {
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

    public List<Tuple<Long, Long>> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<Tuple<Long, Long>> friendsList) {
        this.friendsList = friendsList;
    }

    public List<Long> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Long> messageList) {
        this.messageList = messageList;
    }

    public List<Tuple<Long, Long>> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Tuple<Long, Long>> requestList) {
        this.requestList = requestList;
    }

    @Override
    public String toString() {
        return "Page{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", friendsList=" + friendsList +
                ", messageList=" + messageList +
                ", requestList=" + requestList +
                '}';
    }
}
