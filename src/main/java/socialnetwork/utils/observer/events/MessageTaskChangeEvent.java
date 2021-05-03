package socialnetwork.utils.observer.events;

public class MessageTaskChangeEvent<E> implements Event {
    private ChangeEventType type;
    private E data, oldData;

    public MessageTaskChangeEvent(ChangeEventType type, E data) {
        this.type = type;
        this.data = data;
    }
    public MessageTaskChangeEvent(ChangeEventType type, E data, E oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public E getData() {
        return data;
    }

    public E getOldData() {
        return oldData;
    }
}