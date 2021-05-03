package socialnetwork;

//import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.RequestValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendRequestDatabase;
import socialnetwork.repository.database.FriendshipDatabase;
import socialnetwork.repository.database.MessageDatabase;
import socialnetwork.repository.database.UserDatabase;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.RequestFile;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.Service;
import socialnetwork.ui.UI;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args){
        //String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        System.out.println("1 - file repository");
        System.out.println("2 - database repository");

        Repository<Long, User> userRepository;
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
        Repository<Tuple<Long, Long>, FriendshipRequest> friendRequestsRepository;
        Repository<Long, Message> messagesRepository;
        int op = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = reader.readLine();
            String[] a = line.split(" ");
            op = Integer.parseInt(a[0]);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        switch(op)
        {
            case 1:{
                String userFileName="data/users.csv";
                String friendshipFileName="data/friendships.csv";
                String friendRequestFileName="data/requests.csv";
                String messageFileName="data/messages.csv";
                userRepository = new UserFile(userFileName, new UserValidator());
                friendshipRepository = new FriendshipFile(friendshipFileName, new FriendshipValidator());
                friendRequestsRepository = new RequestFile(friendRequestFileName, new RequestValidator());
                messagesRepository = new MessageFile(messageFileName, new MessageValidator());
            }break;
            case 2:{
                userRepository = new UserDatabase("users", new UserValidator());
                friendshipRepository = new FriendshipDatabase("friendships", new FriendshipValidator());
                friendRequestsRepository = new FriendRequestDatabase("requests", new RequestValidator());
                messagesRepository = new MessageDatabase("messages", new MessageValidator());
            }break;
            default: {
                System.out.println("Non-existent choice!");
                return;
            }
        }

        /*Service service = new Service(userRepository, friendshipRepository, friendRequestsRepository, messagesRepository);
        UI ui = new UI(service);
        ui.meniu();*/

    }
}


