package socialnetwork.utils.observer;

import socialnetwork.utils.observer.events.Event;

public interface Observable<E extends Event> {
    /**
     * adds observer for e
     * @param e - given entity of type Observer<E>
     */
    void addObserver(Observer<E> e);

    /**
     * notifies observers that an event took place
     * @param t - given entity of type E
     */
    void notifyObservers(E t);
}
