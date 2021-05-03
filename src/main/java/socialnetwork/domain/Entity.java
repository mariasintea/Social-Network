package socialnetwork.domain;

import java.io.Serializable;

/**
 * Define an Entity of generic type id
 * @param <ID> - entity id type
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}