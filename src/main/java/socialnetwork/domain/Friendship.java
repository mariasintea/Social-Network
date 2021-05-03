package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Friendship extends Entity<Tuple<Long,Long>> {

    String date;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Friendship() {

        date = dtf.format(LocalDateTime.now());
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * sets the date with given string
     * @param s - data string yyyy-MM-dd
     */
    public void setDate(String s) {
        date = s;
    }
}
