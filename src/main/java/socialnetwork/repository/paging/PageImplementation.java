package socialnetwork.repository.paging;
import java.util.stream.Stream;

public class PageImplementation<T> implements Page<T> {

    private Pageable pageable;
    private Stream<T> content;

    PageImplementation(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return this.pageable;
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }
}
