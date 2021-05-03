package socialnetwork.domain;

public class RequestDTO {
    String username;
    String status;
    String date;

    public RequestDTO(String username, String status, String date) {
        this.username = username;
        this.status = status;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
