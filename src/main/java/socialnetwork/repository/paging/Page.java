package socialnetwork.repository.paging;
import java.util.stream.Stream;

public interface Page<E> {

    /**
     * getter for pageable
     * @return page pageable
     */
    Pageable getPageable();

    /**
     * getter for content
     * @return page content
     */
    Stream<E> getContent();
}
