package socialnetwork.domain.validators;

import socialnetwork.domain.FriendshipRequest;

public class RequestValidator implements Validator<FriendshipRequest> {

    @Override
    public void validate(FriendshipRequest entity) throws ValidationException
    {
        if(entity.getId().getLeft() == null)
            throw new ValidationException("First user does not exist!");
        if(entity.getId().getRight() == null)
            throw new ValidationException("Second user does not exist!");
    }
}
