package socialnetwork.repository.paging;

import socialnetwork.domain.Entity;
import socialnetwork.repository.Repository;

import java.util.function.Function;

public interface PaginatedRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * finds all entities that meets the filterFunction on page
     * @param pageable
     * @param filterFunction - filter function for entities
     * @return page with required entities
     */
    Page<E> findAll(Pageable pageable, Function<E, Boolean> filterFunction);
}
