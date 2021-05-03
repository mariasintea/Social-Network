package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;

public class UI {
    Service service;

    public UI(Service service)
    {
        this.service = service;
    }

    /**
     * creates a menu
     */
    public void meniu(){
        System.out.println("1 - Add User");
        System.out.println("2 - Remove User");
        System.out.println("3 - Add Friendship");
        System.out.println("4 - Remove Friendship");
        System.out.println("5 - Show List of Users");
        System.out.println("6 - Show number of communities");
        System.out.println("7 - Show the most sociable community");
        System.out.println("8 - Show the friends of User");
        System.out.println("9 - Show the friends of User made in Month");
        System.out.println("10 - Show conversation");
        System.out.println("11 - Add message");
        System.out.println("12 - Add reply");
        System.out.println("13 - Add friend request");
        System.out.println("14 - Accept friend request");
        System.out.println("15 - Reject friend request");
        System.out.println("0 - Close app");

        int op = -1;
        while(op != 0)
        {
            try
            {
                System.out.println("Enter an operation:");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line = reader.readLine();
                String[] a = line.split(" ");
                op = Integer.parseInt(a[0]);
                switch(op)
                {
                    case 1: addUserUI(); break;
                    case 2: removeUserUI(); break;
                    case 3: addFriendUI(); break;
                    case 4: removeFriendUI(); break;
                    case 5: showAll(); break;
                    case 6: System.out.println("Number of connected components: " + service.numberOfConnectedComponents()); break;
                    case 7: showLargestComponent(); break;
                    case 8: showFriends(); break;
                    case 9: showFriendsInMonth(); break;
                    case 10: showMessages(); break;
                    case 11: addMessage(); break;
                    case 12: addReply(); break;
                    case 13: addFriendRequest(); break;
                    case 14: approvedFriendRequest(); break;
                    case 15: rejectedFriendRequest(); break;
                    case 20: logIn(); break;
                    case 0: return;
                    default: System.out.println("Wrong operation!");
                }
            }
            catch (IOException ex)
            {
                System.out.println(ex.getMessage());
            }
            catch (ValidationException ex)
            {
                System.out.println("Error: " + ex.getMessage());
            }
            catch (IllegalArgumentException ex)
            {
                System.out.println("Error: " + ex.getMessage());
            }
        }

    }

    private void logIn() throws IOException {
       // System.out.println("Add user: id;surname;first name;username;password");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");

        String user = a[0];
        String password = a[1];
        boolean rez = service.checkPassword(user, password);
        if(rez == TRUE)
            System.out.println("Ok");
        else
            System.out.println("Not ok");
    }

    /**
     * reads data for adding an user
     * @throws IOException
     */
    public void addUserUI() throws IOException {
        System.out.println("Add user: id;surname;first name;username;password");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        User user = new User(a[1], a[2], a[3], a[4], " ");
        user.setId(Long.parseLong(a[0]));
        try
        {
            User newUser = service.addUser(user);
            if(newUser != null)
                System.out.println("ID already exists for " + user.toString());
            else
                System.out.println("User added successfully!");
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * reads data for removing an user
     * @throws IOException
     */
    public void removeUserUI() throws IOException {
        System.out.println("Remove user: id");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(" ");
        Long id = Long.parseLong(a[0]);
        try
        {
            User user = service.removeUser(id);
            if (user != null)
                System.out.println("Deleted " + user.toString());
            else
                System.out.println("There was no user with this ID!");
        }
        catch(IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * prints all users
     */
    public void showAll()
    {
        System.out.println("Users list: ");

        Iterable<User> usersList = service.getAll();
        for(User u : usersList)
            System.out.println(u.toString());
    }

    /**
     * prints the users from the largest component
     */
    public void showLargestComponent()
    {
        System.out.println("The most sociable community: ");

        Iterable<User> usersList = service.getLargestComponent();
        for(User u : usersList)
            System.out.println(u.toString());
    }

    /**
     * reads data for adding a friendship
     * @throws IOException
     */
    public void addFriendUI() throws IOException
    {
        System.out.println("Add friendship: id user1;id user2");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id1 = Long.parseLong(a[0]);
        Long id2 = Long.parseLong(a[1]);
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(id1, id2));
        try
        {
            Friendship newFriendship = service.addFriendship(friendship);
            if(newFriendship != null)
                System.out.println("Friendship already exists for " + friendship.getId().toString());
            else
                System.out.println("Friendship added successfully!");
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * reads data for removing a friendship
     * @throws IOException
     */
    public void removeFriendUI() throws IOException
    {
        System.out.println("Remove friendship: id user1;id user2");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id1 = Long.parseLong(a[0]);
        Long id2 = Long.parseLong(a[1]);
        try
        {
            Friendship friendship = service.removeFriendship(id1, id2);
            if (friendship != null)
                System.out.println("Deleted " + friendship.getId().toString());
            else
                System.out.println("There was no friendship like this!");
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public void showFriends() throws IOException {
        System.out.println("Show friends of user: id");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(" ");
        Long id = Long.parseLong(a[0]);

        List<String> list = service.getFriends(id);
        list.stream().forEach(x->System.out.println(x));
    }

    public void showFriendsInMonth() throws IOException {
        System.out.println("Show friends of user made in month: id;month");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id = Long.parseLong(a[0]);
        String month = a[1];

        List<String> list = service.getFriendsInMonth(id, month);
        list.stream().forEach(x->System.out.println(x));
    }

    private void showMessages() throws IOException {
        System.out.println("Show two messages between two users: id from;id to");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long idFrom = Long.parseLong(a[0]);
        Long idTo = Long.parseLong(a[1]);

        Iterable<String> messageList = service.getMessages(idFrom, idTo);
        for(String message: messageList)
            System.out.println(message);
    }

    public void addMessage() throws IOException {
        System.out.println("Add message: id;message;from;to");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long from = Long.parseLong(a[2]);
        List<Long> to = new ArrayList<Long>();
        String[] users = a[3].split(",");
        for(String userString: users)
        {
            Long user = Long.parseLong(userString);
            to.add(user);
        }
        Message message = new Message(from, to, a[1], null);
        message.setId(Long.parseLong(a[0]));
        try
        {
            service.addMessage(message);
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }


    private void addReply() throws IOException {
        System.out.println("Add reply: id;message;from;to;id message");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long from = Long.parseLong(a[2]);
        Long to = Long.parseLong(a[3]);
        Message reply = new Message(from, Collections.singletonList(to), a[1], null);
        reply.setId(Long.parseLong(a[0]));
        try
        {
            service.addReply(reply, Long.parseLong(a[4]));
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public void addFriendRequest() throws IOException {
        System.out.println("Add friendship request: id user1;id user2");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id1 = Long.parseLong(a[0]);
        Long id2 = Long.parseLong(a[1]);
        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.setId(new Tuple<>(id1, id2));
        try
        {
            FriendshipRequest newFriendRequest = service.addFriendRequestPending(friendshipRequest);
            if(newFriendRequest != null)
                System.out.println("Friend request already exists!");
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public void approvedFriendRequest() throws IOException {
        System.out.println("Accept friendship request: id user1;id user2");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id1 = Long.parseLong(a[0]);
        Long id2 = Long.parseLong(a[1]);
        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.setId(new Tuple<>(id1, id2));
        friendshipRequest.setStatus("approved");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        friendshipRequest.setDate(dtf.format(LocalDateTime.now()));
        try
        {
            service.addFriendRequestApproved(friendshipRequest);
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public void rejectedFriendRequest() throws IOException {
        System.out.println("Reject friendship request: id user1;id user2");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String[] a = line.split(";");
        Long id1 = Long.parseLong(a[0]);
        Long id2 = Long.parseLong(a[1]);
        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.setId(new Tuple<>(id1, id2));
        friendshipRequest.setStatus("rejected");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        friendshipRequest.setDate(dtf.format(LocalDateTime.now()));
        try
        {
            service.addFriendRequestRejected(friendshipRequest);
        }
        catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}

