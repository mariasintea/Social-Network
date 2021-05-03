package socialnetwork.domain;

public class MessageDTO {
    Long id;
    String from;
    String to;
    String date;
    String message;

    public MessageDTO(Long id, String from, String to, String date, String message) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.date = date;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
