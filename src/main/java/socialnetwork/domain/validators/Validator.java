package socialnetwork.domain.validators;

public interface Validator<T> {
    /**
     * validates entity
     * @param entity - given entity
     * @throws ValidationException - if entity not valid
     */
    void validate(T entity) throws ValidationException;
}