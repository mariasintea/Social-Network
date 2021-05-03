package socialnetwork.domain;

public class FriendshipRequest extends Friendship{
    String status;

    public FriendshipRequest() {
        status = "pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
