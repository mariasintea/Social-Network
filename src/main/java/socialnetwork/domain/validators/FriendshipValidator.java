package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator  implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException
    {
        if(entity.getId().getLeft() == null)
            throw new ValidationException("First user does not exist!");
        if(entity.getId().getRight() == null)
            throw new ValidationException("Second user does not exist!");
    }
}
