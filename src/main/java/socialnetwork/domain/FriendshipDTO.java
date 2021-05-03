package socialnetwork.domain;

public class FriendshipDTO {
    String user1;
    String user2;
    String date;

    public FriendshipDTO(String user1, String user2, String date) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
