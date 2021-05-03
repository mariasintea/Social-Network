package socialnetwork.domain.validators;

import socialnetwork.domain.Event;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException
    {
        if(entity.getPage() == null)
            throw new ValidationException("Page does not exist!");
        if(entity.getName() == null)
            throw new ValidationException("Name must not be null!");
    }
}
