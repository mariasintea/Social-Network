package socialnetwork.utils.observer.events;


public class TaskStatusEvent<E> implements Event {
    private TaskExecutionStatusEventType type;
    private E task;

    public TaskStatusEvent(TaskExecutionStatusEventType type, E task) {
        this.task=task;
        this.type=type;
    }

    public E getTask() {
        return task;
    }

    public void setTask(E task) {
        this.task = task;
    }

    public TaskExecutionStatusEventType getType() {
        return type;
    }

    public void setType(TaskExecutionStatusEventType type) {
        this.type = type;
    }
}
