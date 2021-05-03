package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getTo() == null)
            throw new ValidationException("'To' user is null!");
        if(entity.getFrom() == null)
            throw new ValidationException("'From' user is null!");
        if(entity.getMessage() == null)
            throw new ValidationException("Message is null!");
    }
}
