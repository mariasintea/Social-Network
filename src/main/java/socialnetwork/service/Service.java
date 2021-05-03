package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.PaginatedRepository;
import socialnetwork.utils.FilterType;
import socialnetwork.utils.observer.events.ChangeEventType;
import socialnetwork.utils.observer.events.MessageTaskChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Integer.max;
import static java.util.Arrays.fill;

public class Service implements Observable<MessageTaskChangeEvent> {
    /*Repository<Long, User> repoUser;
      Repository<Tuple<Long, Long>, Friendship> repoFriendship;
      Repository<Tuple<Long, Long>, FriendshipRequest> repoFriendshipRequests;
      Repository<Long, Message> repoMessages;
      Repository<Long, Page> repoPages;
      Repository<Long, Event> repoEvents;*/

    PaginatedRepository<Long, User> repoUser;
    PaginatedRepository<Tuple<Long, Long>, Friendship> repoFriendship;
    PaginatedRepository<Tuple<Long, Long>, FriendshipRequest> repoFriendshipRequests;
    PaginatedRepository<Long, Message> repoMessages;
    PaginatedRepository<Long, Page> repoPages;
    PaginatedRepository<Long, Event> repoEvents;
    private boolean viz[];
    private ArrayList<ArrayList<Long>> graph;
    private Long idMessage;
    private Long idUser;
    private Long idPage;
    private Long idEvent;
    private int page = 0;
    private int size = 8;
    Map<FilterType, Integer> numberOfElements;

    /**
     * sets page size with size
     * @param size - int
     */
    public void setPageSize(int size) {
        this.size = size;
    }

    /**
     * sets page with page
     * @param page - int
     */
    public void setPage(int page) {
        this.page = page;
    }

    private List<Observer<MessageTaskChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void notifyObservers(MessageTaskChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

    /**
     * makes a graph out of existing friendships
     */
    private void addFriends()
    {
        for(Friendship f: repoFriendship.findAll())
        {
            graph.get(Math.toIntExact(f.getId().getLeft())).add(f.getId().getRight());
            graph.get(Math.toIntExact(f.getId().getRight())).add(f.getId().getLeft());
        }
    }

    /**
     *
     * @return biggest id from repoUsers that doesn't exist
     */
    private Long getBiggestIDUser() {
        Long maxId = 0L;
        for(User user: repoUser.findAll())
            if(user.getId() > maxId)
                maxId = user.getId();
        return maxId;
    }

    /**
     *
     * @return biggest id from repoMessages that doesn't exist
     */
    private Long getBiggestIDMessage() {
        Long maxId = 0L;
        for(Message message: repoMessages.findAll())
            if(message.getId() > maxId)
                maxId = message.getId();
        return maxId;
    }

    /**
     *
     * @return biggest id from repoPages that doesn't exist
     */
    private Long getBiggestIDPage() {
        Long maxId = 0L;
        for(Page page: repoPages.findAll())
            if(page.getId() > maxId)
                maxId = page.getId();
        return maxId;
    }

    /**
     *
     * @return biggest id from repoEvents that doesn't exist
     */
    private Long getBiggestIDEvent() {
        Long maxId = 0L;
        for(Event event: repoEvents.findAll())
            if(event.getId() > maxId)
                maxId = event.getId();
        return maxId;
    }

    public Service(PaginatedRepository<Long, User> repoUser, PaginatedRepository<Tuple<Long, Long>, Friendship> repoFriendship, PaginatedRepository<Tuple<Long, Long>, FriendshipRequest> repoFriendshipRequests, PaginatedRepository<Long, Message> repoMessages, PaginatedRepository<Long, Page> repoPages, PaginatedRepository<Long, Event> repoEvents) {
        this.repoFriendship = repoFriendship;
        this.repoUser = repoUser;
        this.repoFriendshipRequests = repoFriendshipRequests;
        this.repoMessages = repoMessages;
        this.repoPages = repoPages;
        this.repoEvents = repoEvents;
        this.idMessage = getBiggestIDMessage() + 1;
        this.idUser = getBiggestIDUser() + 1;
        this.idPage = getBiggestIDPage() + 1;
        this.idEvent = getBiggestIDEvent() + 1;
        this.graph = new ArrayList<>();
        for(int i = 1;i < 10000;i++)
            graph.add(new ArrayList<Long>());
        this.viz = new boolean[10000];
        addFriends();
        numberOfElements = new HashMap<>();
    }

    /**
     * adds user in database
     * @param messageTask - user
     * @return the user that was before with the given id
     */
    public User addUser(User messageTask) {
        messageTask.setId(idUser++);
        User task = repoUser.save(messageTask);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, messageTask));
        return task;
    }

    /**
     * searches a user by username
     * @param username - string
     * @return user
     */
    public User searchUser(String username){
        for(User user: repoUser.findAll())
            if(user.getUsername().equals(username))
                return user;
        return null;
    }

    /**
     * searches a user by id
     * @param id - Long
     * @return user
     */
    public User searchUserByID(Long id){
        return repoUser.findOne(id);
    }

    /**
     * checks password for user
     * @param username - user's username
     * @param password - user's password
     * @return TRUE - they match, FALSE - otherwise
     */
    public boolean checkPassword(String username, String password){
        User user = searchUser(username);
        return password.equals(user.getPassword());
    }

    /**
     * removes user from database
     * @param Id - Long
     * @return the user that was before with the given id
     */
    public User removeUser(Long Id) {
        User task = repoUser.delete(Id);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.DELETE, task));
        return task;
    }

    /**
     * updates user from database
     * @param user - User
     */
    public void updateUser(User user) {
        repoUser.update(user);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.UPDATE, user));
    }

    /**
     * @return all users
     */
    public Iterable<User> getAll(){
        return repoUser.findAll();
    }

    /**
     * gets all users from page and sets the number of elements for USERS
     * @param id - Long
     * @return all users from page that don't have the given id
     */
    public Set<User> getAllUsers(Long id){
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<User> users = repoUser.findAll(pageable, (User user)->{ return (user.getId() != id);});
        numberOfElements.put(FilterType.USERS, ((List<User>) repoUser.findAll()).stream().filter(user->{ return (user.getId() != id);}).collect(Collectors.toList()).size());
        return users.getContent().collect(Collectors.toSet());
    }

    /**
     * adds friendship in database
     * @param friendship - Friendship
     * @return the friendship that was before with the given id
     * @throws IllegalArgumentException
     *          if one of the users is null
     */
    public Friendship addFriendship(Friendship friendship)
    {
        Long Id1 = friendship.getId().getLeft();
        Long Id2 = friendship.getId().getRight();
        if(repoFriendship.findOne(new Tuple<Long, Long>(Id2, Id1)) != null)
            throw new IllegalArgumentException("Friendship already exists!");

        if (repoUser.findOne(Id1) == null || repoUser.findOne(Id2) == null)
            throw new IllegalArgumentException("Non existent user!");

        Friendship newFriendship = repoFriendship.save(friendship);
        if(newFriendship == null) {
            graph.get(Math.toIntExact(Id1)).add(Id2);
            graph.get(Math.toIntExact(Id2)).add(Id1);
        }
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, friendship));
        return newFriendship;
    }

    /**
     * removes friendship from database
     * @param Id1 - Long, id user 1
     * @param Id2 - Long, id user 2
     * @return the friendship that was before with the given id
     * @throws IllegalArgumentException
     *          if one of the users is null
     */
    public Friendship removeFriendship(Long Id1, Long Id2)
    {
        if(repoFriendship.findOne(new Tuple<Long, Long>(Id1, Id2)) == null) {
            if (repoFriendship.findOne(new Tuple<Long, Long>(Id2, Id1)) != null) {
                Friendship friendship = repoFriendship.delete(new Tuple<Long, Long>(Id2, Id1));
                if(friendship != null) {
                    graph.get(Math.toIntExact(Id1)).remove(Id2);
                    graph.get(Math.toIntExact(Id2)).remove(Id1);
                }
                notifyObservers(new MessageTaskChangeEvent(ChangeEventType.DELETE, friendship));
                return friendship;
            } else
                throw new IllegalArgumentException("Friendship doesn't exists!");
        }
        else {
            Friendship friendship = repoFriendship.delete(new Tuple<Long, Long>(Id1, Id2));
            if(friendship != null) {
                graph.get(Math.toIntExact(Id1)).remove(Id2);
                graph.get(Math.toIntExact(Id2)).remove(Id1);
            }
            notifyObservers(new MessageTaskChangeEvent(ChangeEventType.DELETE, friendship));
            return friendship;
        }
    }

    /**
     * goes through all the users from the current component and marks them as visited
     * @param current - current user
     */
    private void dfs(int current)
    {
        List<Long> friendList = graph.get(current);
        for(Long idFriend: friendList){
            int neighbour = Math.toIntExact(idFriend);
            if(!viz[neighbour])
            {
                viz[neighbour] = Boolean.TRUE;
                dfs(neighbour);
            }
        }
    }

    /**
     * counts all connected components
     * @return number of connected components
     */
    public int numberOfConnectedComponents()
    {
        fill(viz, Boolean.FALSE);
        int nrComponents = 0;
        for(User user: repoUser.findAll())
        {
            int id = Math.toIntExact(user.getId());
            if(viz[id] == Boolean.FALSE)
            {
                viz[id] = Boolean.TRUE;
                dfs(id);
                nrComponents++;
            }
        }
        return nrComponents;
    }

    /**
     * counts the users from the current connected component
     * @param current - the current user
     * @return the number of users from the current connected component
     */
    private int dfsCount(int current)
    {
        List<Long> friendList = graph.get(current);
        for(Long idFriend: friendList){
            int neighbour = Math.toIntExact(idFriend);
            if(!viz[neighbour])
            {
                viz[neighbour] = Boolean.TRUE;
                return 1 + dfsCount(neighbour);
            }
        }

        return 1;
    }

    /**
     * finds the largest connected component
     * @return one id from the largest connected component
     */
    private Long LargestComponent()
    {
        fill(viz, Boolean.FALSE);
        int maxComponent = 0;
        Long IDMaxComponent = 1L;
        for(User user: repoUser.findAll())
        {
            int id = Math.toIntExact(user.getId());
            if(viz[id] == Boolean.FALSE)
            {
                viz[id] = Boolean.TRUE;
                int nrVertices = dfsCount(id);
                if(nrVertices > maxComponent)
                {
                    maxComponent = nrVertices;
                    IDMaxComponent = user.getId();
                }
            }
        }
        return IDMaxComponent;
    }

    /**
     * creates a list with the users of the connected component
     * @param current - current user Id
     * @param friendsList - the list containing the users of the connected component
     */
    private void dfsAddComponents(int current, List<User> friendsList)
    {
        List<Long> friendList = graph.get(current);
        for(Long idFriend: friendList){
            int neighbour = Math.toIntExact(idFriend);
            if(!viz[neighbour])
            {
                viz[neighbour] = Boolean.TRUE;
                User user_neighbour = repoUser.findOne(idFriend);
                friendsList.add(user_neighbour);
                dfsAddComponents(neighbour, friendsList);
            }
        }
    }

    /**
     * finds the largest connected component
     * @return the users id from the connected component
     */
    public Iterable<User> getLargestComponent()
    {
        Long ID = LargestComponent();
        User user= repoUser.findOne(ID);
        List<User> friendsList = new ArrayList<User>();
        fill(viz, Boolean.FALSE);
        dfsAddComponents(Math.toIntExact(ID), friendsList);
        return friendsList;
    }

    /**
     * gets all friends of user with given id
     * @param ID - Long
     * @return all friends of user with given id
     */
    public List<String> getFriends(Long ID)
    {
        List<Friendship> list = StreamSupport.stream(repoFriendship.findAll().spliterator(), false).collect(Collectors.toList());
        return list.stream().filter(x->(x.getId().getLeft() == ID || x.getId().getRight() == ID))
                .map(x->{
                    if(x.getId().getRight() == ID)
                        return repoUser.findOne(x.getId().getLeft()).getFirstName() + "|" + repoUser.findOne(x.getId().getLeft()).getLastName() + "|" + x.getDate();
                    else
                        return repoUser.findOne(x.getId().getRight()).getFirstName() + "|" + repoUser.findOne(x.getId().getRight()).getLastName() + "|" + x.getDate();
                }).collect(Collectors.toList());
    }

    /**
     * gets all friends from page that are friends with user with given id and
     * sets the number of elements for FRIENDS
     * @param ID - Long
     * @return all friends from page that are friends with user with given id
     */
    public Set<User> getAllFriends(Long ID)
    {
        List<Friendship> list = StreamSupport.stream(repoFriendship.findAll().spliterator(), false).collect(Collectors.toList());
        List<User> usersList = list.stream().filter(x->(x.getId().getLeft() == ID || x.getId().getRight() == ID))
                .map(x->{
                    if(x.getId().getRight() == ID)
                        return repoUser.findOne(x.getId().getLeft());
                    else
                        return repoUser.findOne(x.getId().getRight());
                }).collect(Collectors.toList());
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<User> users = repoUser.findAll(pageable, (User user)->{return usersList.contains(user);});
        numberOfElements.put(FilterType.FRIENDS, ((List<User>) repoUser.findAll()).stream().filter(user->{ return usersList.contains(user);}).collect(Collectors.toList()).size());
        return users.getContent().collect(Collectors.toSet());
    }

    /**
     * gets all friends that are friends with user with given id and
     *      befriended in given month
     * @param ID - Long
     * @return all friends that are friends with user with given id and
     *      befriended in given month
     */
    public List<String> getFriendsInMonth(Long ID, String month)
    {
        List<Friendship> list = StreamSupport.stream(repoFriendship.findAll().spliterator(), false).collect(Collectors.toList());
        return list.stream().filter(x->(x.getId().getLeft() == ID || x.getId().getRight() == ID))
                .filter(x-> {
                    String[] dateDetails =x.getDate().split("-");
                    return dateDetails[1].equals(month);
                })
                .map(x->{
                    if(x.getId().getRight() == ID)
                        return repoUser.findOne(x.getId().getLeft()).getFirstName() + "|" + repoUser.findOne(x.getId().getLeft()).getLastName() + "|" + x.getDate();
                    else
                        return repoUser.findOne(x.getId().getRight()).getFirstName() + "|" + repoUser.findOne(x.getId().getRight()).getLastName() + "|" + x.getDate();
                }).collect(Collectors.toList());
    }

    /**
     * adds request into database
     * @param friendshipRequest - FriendshipRequest
     * @return request that was before
     */
    public FriendshipRequest addFriendRequestPending(FriendshipRequest friendshipRequest)
    {
        Long Id1 = friendshipRequest.getId().getLeft();
        Long Id2 = friendshipRequest.getId().getRight();
        if(repoFriendshipRequests.findOne(new Tuple<Long, Long>(Id2, Id1)) != null)
            throw new IllegalArgumentException("Friend Request already exists!");
        FriendshipRequest newFriendshipRequest = repoFriendshipRequests.save(friendshipRequest);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, friendshipRequest));
        return newFriendshipRequest;
    }

    /**
     * updates request into 'approved'
     * @param friendshipRequest - FriendshipRequest
     */
    public void addFriendRequestApproved(FriendshipRequest friendshipRequest)
    {
        FriendshipRequest request = repoFriendshipRequests.findOne(friendshipRequest.getId());
        if (request == null || !request.getStatus().equals("pending"))
            throw new IllegalArgumentException("Non existent request!");
        repoFriendshipRequests.update(friendshipRequest);
        addFriendship(friendshipRequest);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.UPDATE, friendshipRequest));
    }

    /**
     * updates request into 'rejected'
     * @param friendshipRequest - FriendshipRequest
     */
    public void addFriendRequestRejected(FriendshipRequest friendshipRequest)
    {
        FriendshipRequest request = repoFriendshipRequests.findOne(friendshipRequest.getId());
        if (request == null || !request.getStatus().equals("pending"))
                throw new IllegalArgumentException("Non existent request!");
        repoFriendshipRequests.update(friendshipRequest);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.UPDATE, friendshipRequest));
    }

    /**
     * transforms a received request from FriendshipRequest to RequestDTO
     * @param request - FriendshipRequest
     * @return RequestDTO
     */
    private RequestDTO fromFriendshipRequestToRequestDTO_Received(FriendshipRequest request)
    {
        RequestDTO dtoRequest = new RequestDTO(repoUser.findOne(request.getId().getLeft()).getUsername(), request.getStatus(), request.getDate());
        return dtoRequest;
    }

    /**
     * transforms a sent request from FriendshipRequest to RequestDTO
     * @param request - FriendshipRequest
     * @return RequestDTO
     */
    private RequestDTO fromFriendshipRequestToRequestDTO_Sent(FriendshipRequest request)
    {
        RequestDTO dtoRequest = new RequestDTO(repoUser.findOne(request.getId().getRight()).getUsername(), request.getStatus(), request.getDate());
        return dtoRequest;
    }

    /**
     * gets all 'pending' requests from page that were received by user with given id and
     * sets the number of elements for RECEIVED_REQUESTS
     * @param ID - Long
     * @return all 'pending' requests from page that were received by user with given id
     */
    public Set<RequestDTO> getAllReceivedFriendRequests(Long ID)
    {
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<FriendshipRequest> requests = repoFriendshipRequests.findAll(pageable, (FriendshipRequest request)->{return request.getStatus().equals("pending") && request.getId().getRight() == ID;});
        numberOfElements.put(FilterType.RECEIVED_REQUESTS, ((List<FriendshipRequest>) repoFriendshipRequests.findAll()).stream().filter(request->{return request.getStatus().equals("pending") && request.getId().getRight() == ID;}).collect(Collectors.toList()).size());
        return requests.getContent().map(x->fromFriendshipRequestToRequestDTO_Received(x)).collect(Collectors.toSet());
    }

    /**
     * gets all requests from page that were received by user with given id and
     * sets the number of elements for REQUESTS
     * @param ID - Long
     * @return all requests from page that were received by user with given id
     */
    public Set<RequestDTO> getAllFriendRequests(Long ID)
    {
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<FriendshipRequest> requests = repoFriendshipRequests.findAll(pageable, (FriendshipRequest request)->{return request.getId().getRight() == ID;});
        numberOfElements.put(FilterType.REQUESTS, ((List<FriendshipRequest>) repoFriendshipRequests.findAll()).stream().filter(request->{return request.getId().getRight() == ID;}).collect(Collectors.toList()).size());
        return requests.getContent().map(x->fromFriendshipRequestToRequestDTO_Received(x)).collect(Collectors.toSet());
    }

    /**
     * gets all requests from page that were sent by user with given id and
     * sets the number of elements for SENT_REQUESTS
     * @param ID - Long
     * @return all requests from page that were sent by user with given id
     */
    public Set<RequestDTO> getAllSentFriendRequests(Long ID)
    {
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<FriendshipRequest> requests = repoFriendshipRequests.findAll(pageable, (FriendshipRequest request)->{return request.getStatus().equals("pending") && request.getId().getLeft() == ID;});
        numberOfElements.put(FilterType.SENT_REQUESTS, ((List<FriendshipRequest>) repoFriendshipRequests.findAll()).stream().filter(request->{return request.getStatus().equals("pending") && request.getId().getLeft() == ID;}).collect(Collectors.toList()).size());
        return requests.getContent().map(x->fromFriendshipRequestToRequestDTO_Sent(x)).collect(Collectors.toSet());
    }

    /**
     * deletes request with given id
     * @param requestId - request's id
     */
    public void deleteRequest(Tuple<Long, Long> requestId)
    {
        Long Id1 = requestId.getLeft();
        Long Id2 = requestId.getRight();
        FriendshipRequest rez;
        if(repoFriendshipRequests.findOne(new Tuple<Long, Long>(Id1, Id2)) == null) {
            if (repoFriendshipRequests.findOne(new Tuple<Long, Long>(Id2, Id1)) != null)
                rez = repoFriendshipRequests.delete(new Tuple<Long, Long>(Id2, Id1));
            else
                throw new IllegalArgumentException("Friend Request doesn't exists!");
        }
        else
            rez = repoFriendshipRequests.delete(requestId);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.DELETE, rez));
    }

    /**
     * transforms a message from Message to MessageDTO
     * @param message - Message
     * @return MessageDTO
     */
    private MessageDTO fromMessageToMessageDTO(Message message)
    {
        String listTo = message.getTo().stream().map(x->repoUser.findOne(x).getUsername()).collect(Collectors.joining(", "));
        MessageDTO messageDTO = new MessageDTO(message.getId(), repoUser.findOne(message.getFrom()).getUsername(), listTo, message.getDate(), message.getMessage());
        return messageDTO;
    }

    /**
     * gets all messages from page that are not replies and contain user with given id
     * and sets the number of elements for MESSAGES
     * @param id - Long
     * @return all messages from page that are not replies and contain user with given id
     */
    public Set<MessageDTO> getAllMessages(Long id){
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<Message> messages = repoMessages.findAll(pageable, (Message message)->{ return (message.getReply() == null && (message.getFrom() == id || message.getTo().contains(id)));});
        numberOfElements.put(FilterType.MESSAGES, ((List<Message>) repoMessages.findAll()).stream().filter(message->{ return (message.getReply() == null && (message.getFrom() == id || message.getTo().contains(id)));}).collect(Collectors.toList()).size());
        return messages.getContent().map(x->fromMessageToMessageDTO(x)).collect(Collectors.toSet());
    }

    /**
     * gets all messages from page that are replies to message with idMessage and
     * contain user with given idUse and sets the number of elements for CONVERSATIONS
     * @param idMessage - Long
     * @param idUser - Long
     * @return all messages from page that are replies to message with idMessage and
     *      contain user with given idUser
     */
    public Set<MessageDTO> getConversation(Long idMessage, Long idUser){
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<Message> messages = repoMessages.findAll(pageable, (Message message)->{ return message.getId() == idMessage || (message.getReply() == idMessage && (message.getTo().contains(idUser) || message.getFrom() == idUser));});
        numberOfElements.put(FilterType.CONVERSATIONS, ((List<Message>) repoMessages.findAll()).stream().filter(message->{ return message.getId() == idMessage || (message.getReply() == idMessage && (message.getTo().contains(idUser) || message.getFrom() == idUser));}).collect(Collectors.toList()).size());
        return messages.getContent().map(x->fromMessageToMessageDTO(x)).collect(Collectors.toSet());
    }

    /**
     * gets all messages between user with idFrom and user with idTo
     * @param idFrom - one user's id
     * @param idTo - one user's id
     * @return all messages between user with idFrom and user with idTo
     */
    public Iterable<String> getMessages(Long idFrom, Long idTo) {
        List<String> messageList = new ArrayList<String>();
        List<Long> idListTo = new ArrayList<>();
        idListTo.add(idTo);
        List<Long> idListFrom = new ArrayList<>();
        idListFrom.add(idFrom);
        List<Long> beginMessages = new ArrayList<>();
        for(Message message:repoMessages.findAll())
            if(message.getReply() == null && (message.getFrom() == idFrom && message.getTo().equals(idListTo) || message.getFrom() == idTo && message.getTo().equals(idListFrom)))
                beginMessages.add(message.getId());
        for(Message message:repoMessages.findAll())
            if(beginMessages.contains(message.getReply()) || beginMessages.contains(message.getId())){
                if (message.getFrom() == idFrom && message.getTo().equals(idListTo)) {
                    String line = "from " + repoUser.findOne(idFrom).toString2() + " to " + repoUser.findOne(idTo).toString2() + " on " + message.getDate() + ":" + message.getMessage();
                    messageList.add(line);
                } else if (message.getFrom() == idTo && message.getTo().equals(idListFrom)) {
                    String line = "from " + repoUser.findOne(idTo).toString2() + " to " + repoUser.findOne(idFrom).toString2() + " on " + message.getDate() + ":" + message.getMessage();
                    messageList.add(line);
                }
            }
        return messageList;
    }

    /**
     * searches a message id
     * @param message - Message
     * @return id of message
     */
    public Long searchMessageID(Message message){
        for(Message m: repoMessages.findAll())
            if(m.getFrom() == message.getFrom() && m.getTo().equals(message.getTo()) && m.getDate().equals(message.getDate()) && m.getMessage().equals(message.getMessage()))
                return m.getId();
        return null;
    }

    /**
     * adds message to database
     * @param message - Message
     * @throws IllegalArgumentException if there is no 'from' or 'to' user
     */
    public void addMessage(Message message) {
        message.setId(idMessage++);
        if (repoUser.findOne(message.getFrom()) == null)
            throw new IllegalArgumentException("Non existent 'from' user!");
        for(Long user: message.getTo())
            if (repoUser.findOne(user) == null)
                throw new IllegalArgumentException("Non existent 'to' user!");
        repoMessages.save(message);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, message));
    }

    /**
     * adds reply to message to database
     * @param reply - message
     * @param id - id of the message the reply goes
     * @throws IllegalArgumentException if there is no 'from' User
     */
    public void addReply(Message reply, Long id) {
        reply.setId(idMessage++);
        if (repoUser.findOne(reply.getFrom()) == null)
            throw new IllegalArgumentException("Non existent 'from' user!");
        reply.setReply(id);
        repoMessages.save(reply);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, reply));
    }

    /**
     * gets all messages from a certain period of time and from a certain user
     * @param id - user's id
     * @param startDate - date 'yyyy-MM-dd'
     * @param endDate - date 'yyyy-MM-dd'
     * @return all messages from a certain period of time and from a certain user
     */
    public List<MessageDTO> getMessagesFromPeriod(Long id, String startDate, String endDate)
    {
        List<MessageDTO> messageList = new ArrayList<>();
        for(Message message:repoMessages.findAll())
            if(message.getDate().compareTo(startDate)>=0 && message.getDate().compareTo(endDate)<=0){
                if (message.getTo().contains(id)) {
                    MessageDTO messageDTO = fromMessageToMessageDTO(message);
                    messageList.add(messageDTO);
                }
            }
        return messageList;
    }

    /**
     * gets all friends from a certain period of time and from a certain user
     * @param id - user's id
     * @param startDate - date 'yyyy-MM-dd'
     * @param endDate - date 'yyyy-MM-dd'
     * @return all friends from a certain period of time and from a certain user
     */
    public List<FriendDTO> getFriendsFromPeriod(Long id, String startDate, String endDate)
    {
        List<FriendDTO> friendsList = new ArrayList<>();
        for(Friendship friendship:repoFriendship.findAll())
            if(friendship.getDate().compareTo(startDate)>=0 && friendship.getDate().compareTo(endDate)<=0){
                if (friendship.getId().getLeft()== id){
                    User user = repoUser.findOne(friendship.getId().getRight());
                    friendsList.add(new FriendDTO(user.getUsername(), friendship.getDate()));
                }
                else
                    if(friendship.getId().getRight() == id){
                        User user = repoUser.findOne(friendship.getId().getLeft());
                        friendsList.add(new FriendDTO(user.getUsername(), friendship.getDate()));
                    }
            }
        return friendsList;
    }

    /**
     * gets all messages between a certain user and his friend
     * @param idUser - user's id
     * @param idFriend - friend's id
     * @param startDate - date 'yyyy-MM-dd'
     * @param endDate - date 'yyyy-MM-dd'
     * @return all messages from a certain period of time and from a certain user
     */
    public List<MessageDTO> getMessagesFromFriendAndPeriod(Long idUser, Long idFriend, String startDate, String endDate) {
        List<MessageDTO> messageList = new ArrayList<>();
        List<Long> idListUser = new ArrayList<>();
        idListUser.add(idUser);
        for(Message message:repoMessages.findAll())
            if(message.getDate().compareTo(startDate)>=0 && message.getDate().compareTo(endDate)<=0){
                if (message.getTo().equals(idListUser) && message.getFrom() == idFriend) {
                    MessageDTO messageDTO = fromMessageToMessageDTO(message);
                    messageList.add(messageDTO);
                }
            }
        return messageList;
    }

    /**
     * gets all users from page that are involved in a conversation
     * and sets the number of elements for USERS_MESSAGE
     * @param user - User
     * @param message - Message
     * @return all users from page that are involved in a conversation
     */
    public Set<User> getAllUsersFromMessage(MessageDTO message, User user)
    {
        List<User> usersList = new ArrayList<>();
        if(message != null){
            if(!message.getFrom().equals(user.getUsername()))
                usersList.add(searchUser(message.getFrom()));
            for(String username: message.getTo().split(","))
                if(searchUser(username).getId() != user.getId())
                    usersList.add(searchUser(username));
        }
        else
            {
                for(User u: repoUser.findAll())
                    if(u.getId() != user.getId())
                        usersList.add(u);
            }

        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<User> users = repoUser.findAll(pageable, (User u)->{return usersList.contains(u);});
        numberOfElements.put(FilterType.USERS_MESSAGE, ((List<User>) repoUser.findAll()).stream().filter(u->{ return usersList.contains(u);}).collect(Collectors.toList()).size());
        return users.getContent().collect(Collectors.toSet());
    }

    /**
     * adds page to database
     * @param messageTask - Page
     * @return the page that was before
     */
    public Page addPage(Page messageTask) {
        messageTask.setId(idPage++);
        Page task = repoPages.save(messageTask);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, messageTask));
        return task;
    }

    /**
     * searches a page by name and surname
     * @param name - user's name
     * @param surname - user's surname
     * @return page with given name and surname
     */
    public Page searchPage(String name, String surname){
        for(Page page: repoPages.findAll())
            if(page.getName().equals(name) && page.getSurname().equals(surname))
                return page;
        return null;
    }

    /**
     * updates page into database
     * @param page - Page
     * @return the page that was before
     */
    public Page updatePage(Page page) {
        Page task = repoPages.update(page);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.UPDATE, page));
        return task;
    }

    /**
     * adds event to database
     * @param messageTask - Event
     * @return the event that was before
     */
    public Event addEvent(Event messageTask) {
        messageTask.setId(idEvent++);
        Event task = repoEvents.save(messageTask);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, messageTask));
        return task;
    }

    /**
     * updates event into database
     * @param event - Event
     * @return the event that was before
     */
    public Event updateEvent(Event event) {
        Event task = repoEvents.update(event);
        notifyObservers(new MessageTaskChangeEvent(ChangeEventType.UPDATE, event));
        return task;
    }

    /**
     * searches an event by id
     * @param ID - Long
     * @return event with given id
     */
    public Event searchEvent(Long ID){
        for(Event event: repoEvents.findAll())
            if(event.getId() == ID)
                return event;
        return null;
    }

    /**
     * transforms an event from Event to EventDTO
     * @param event - Event
     * @return EventDTO
     */
    private EventDTO fromEventToEventDTO(Event event)
    {
        return new EventDTO(repoPages.findOne(event.getPage()).getName() + " " + repoPages.findOne(event.getPage()).getSurname(), event.getName(), event.getDate(), event.getId());
    }

    /**
     * gets all events from page that don't belong to eventPage
     * and sets the number of elements for EVENTS
     * @param eventPage - page
     * @return all events from page that don't belong to eventPage
     */
    public Set<EventDTO> getAllEvents(Page eventPage) {
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<Event> events = repoEvents.findAll(pageable, (Event event)->{ return event.getPage() != eventPage.getId();});
        numberOfElements.put(FilterType.EVENTS, ((List<Event>) repoEvents.findAll()).stream().filter(event->{ return event.getPage() != eventPage.getId();}).collect(Collectors.toList()).size());
        return events.getContent().map(x->fromEventToEventDTO(x)).collect(Collectors.toSet());
    }

    /**
     * gets all events that user has subscribed to
     * and sets the number of elements for SUBSCRIBED_EVENTS
     * @param user - User
     * @return all events that user has subscribed to
     */
    public Set<EventDTO> getAllSubscribedEvents(User user) {
        Pageable pageable = new PageableImplementation(page, size);
        socialnetwork.repository.paging.Page<Event> events = repoEvents.findAll(pageable, (Event event)->{ return event.getSubscribers().contains(user.getId());});
        numberOfElements.put(FilterType.SUBSCRIBED_EVENTS, ((List<Event>) repoEvents.findAll()).stream().filter(event->{ return event.getSubscribers().contains(user.getId());}).collect(Collectors.toList()).size());
        return events.getContent().map(x->fromEventToEventDTO(x)).collect(Collectors.toSet());
    }

    /**
     * gets number of pages of a paginated repository with given type
     * @param type - FilterType
     * @return
     */
    public int getNumberOfPages(FilterType type)
    {
        return max(1, (numberOfElements.get(type) + 7)/8);
    }
}
