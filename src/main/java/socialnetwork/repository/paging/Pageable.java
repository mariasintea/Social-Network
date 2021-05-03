package socialnetwork.repository.paging;

public interface Pageable {
    /**
     * getter for page number
     * @return page number
     */
    int getPageNumber();

    /**
     * getter for page size
     * @return page size
     */
    int getPageSize();
}
