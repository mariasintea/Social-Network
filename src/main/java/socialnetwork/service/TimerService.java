package socialnetwork.service;

import java.util.HashMap;
import java.util.Timer;

public class TimerService {
    private HashMap<Long, Timer> timers;
    private HashMap<Long, Timer> timers5Min;

    public TimerService() {

        this.timers = new HashMap<>();
        this.timers5Min = new HashMap<>();
    }

    /**
     * adds timer
     * @param id - user's id
     * @param timer - timer
     */
    public void addTimer(Long id, Timer timer)
    {
        timers.put(id, timer);
    }

    /**
     * finds timer with given id
     * @param id - user's id
     * @return timer
     */
    public Timer getTimer(Long id)
    {
        return timers.get(id);
    }

    /**
     * adds timer with 5Min type
     * @param id - user's id
     * @param timer - timer
     */
    public void addTimer5Min(Long id, Timer timer) {
        timers5Min.put(id, timer);
    }

    /**
     * finds timer with given id with 5Min type
     * @param id - user's id
     * @return timer
     */
    public Timer getTimer5Min(Long id)
    {
        if(!timers5Min.containsKey(id))
            return null;
        return timers5Min.get(id);
    }
}
