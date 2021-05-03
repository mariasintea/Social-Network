package socialnetwork.domain;

import java.util.List;

public class Event extends Entity<Long>{
    Long page;
    String name;
    String date;
    List<Long> subscribers;

    public Event(Long page, String name, String date, List<Long> subscribers) {
        this.page = page;
        this.name = name;
        this.date = date;
        this.subscribers = subscribers;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Long> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Long> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return "Event{" +
                "page=" + page +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
