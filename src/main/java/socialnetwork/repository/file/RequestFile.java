package socialnetwork.repository.file;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class RequestFile extends AbstractFileRepository<Tuple<Long, Long>, FriendshipRequest>{

    public RequestFile(String fileName, Validator<FriendshipRequest> validator) {
        super(fileName, validator);
    }

    @Override
    public FriendshipRequest extractEntity(List<String> attributes) {
        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.setId(new Tuple<Long, Long>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));
        friendshipRequest.setDate(attributes.get(2));
        friendshipRequest.setStatus(attributes.get(3));
        return friendshipRequest;
    }

    @Override
    protected String createEntityAsString(FriendshipRequest entity) {
        return entity.getId().toString() + ";" + entity.getDate() + ";" + entity.getStatus();
    }
}
