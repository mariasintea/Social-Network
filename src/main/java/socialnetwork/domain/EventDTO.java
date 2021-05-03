package socialnetwork.domain;

public class EventDTO{
    String organizer;
    String name;
    String date;
    Long ID;

    public EventDTO(String organizer, String name, String date, Long ID) {
        this.organizer = organizer;
        this.name = name;
        this.date = date;
        this.ID = ID;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
