package socialnetwork.utils.observer;

import socialnetwork.utils.observer.events.Event;

public interface Observer<E extends Event> {
    /**
     * updates e
     * @param e - given entity of type E
     */
    void update(E e);
}