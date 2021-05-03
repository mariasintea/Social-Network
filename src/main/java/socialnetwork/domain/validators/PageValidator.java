package socialnetwork.domain.validators;

import socialnetwork.domain.Page;

public class PageValidator implements Validator<Page> {

    @Override
    public void validate(Page entity) throws ValidationException
    {
        if(entity.getSurname() == null)
            throw new ValidationException("Surname must not be null!");
        if(entity.getName() == null)
            throw new ValidationException("Name must not be null!");
    }
}
