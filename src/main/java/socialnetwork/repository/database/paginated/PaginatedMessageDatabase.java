package socialnetwork.repository.database.paginated;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PaginatedRepository;
import socialnetwork.repository.paging.Paginator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginatedMessageDatabase implements PaginatedRepository<Long, Message> {

    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<Message> validator;

    public PaginatedMessageDatabase(String tableName, Validator<Message> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type Message having a specified list of @code attributes
     * @param attributes
     * @return an entity of type Message
     */
    public Message extractEntity(List<String> attributes) {

        Long from = Long.parseLong(attributes.get(3));
        List<Long> to = new ArrayList<Long>();
        String[] users = attributes.get(4).split(",");
        for(String userString: users)
        {
            Long user = Long.parseLong(userString);
            to.add(user);
        }
        Long id_reply;
        if(attributes.get(5) != null)
            id_reply = Long.parseLong(attributes.get(5));
        else
            id_reply = null;
        Message message = new Message(from, to, attributes.get(1), id_reply);
        message.setId(Long.parseLong(attributes.get(0)));
        message.setDate(attributes.get(2));
        return message;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(Message entity) {
        return entity.getId() + ";" + entity.getMessage() + ";" + entity.getDate() + ";" + entity.getFrom() + ";" + entity.getTo().stream().map(Objects::toString).collect(Collectors.joining(","));
    }

    @Override
    public Message findOne(Long id){
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement();
            String query = "SELECT * FROM " + tableName + " WHERE id = " + id + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                List<String> attr = new ArrayList<>();
                attr.add(rs.getString(1));
                attr.add(rs.getString(2));
                attr.add(rs.getString(3));
                attr.add(rs.getString(4));
                attr.add(rs.getString(5));
                attr.add(rs.getString(6));
                return extractEntity(attr);
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> list = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM " + tableName + ";";
            ResultSet rs = stmt.executeQuery(query);
            rs.beforeFirst();
            while(!rs.isLast()) {
                if (rs.next()) {
                    List<String> attr = new ArrayList<>();
                    attr.add(rs.getString(1));
                    attr.add(rs.getString(2));
                    attr.add(rs.getString(3));
                    attr.add(rs.getString(4));
                    attr.add(rs.getString(5));
                    attr.add(rs.getString(6));
                    Message e = extractEntity(attr);
                    list.add(e);
                }
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return list;
    }

    @Override
    public void addAll(Iterable<Message> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(Message entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "insert into " + tableName + " values('"+ args[0] + "', '" + args[1] +"', '" + args[2] +"', '" + args[3] +"', '" + args[4] +"', '" + args[5] +"')";
                Statement stmt = con.createStatement();
                int x = stmt.executeUpdate(query);
                if (x == 0)
                    throw new Exception("Insert Failed");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    @Override
    public Message save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        if(findOne(entity.getId()) != null) {
            return entity;
        }
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            String row = createEntityAsString(entity);
            String[] args = row.split(";");
            String query;
            if(entity.getReply() != null)
                query = "insert into " + tableName +" values('"+ args[0] + "', '" + args[1] +"', '" + args[2] + "', '" + args[3] +"', '" + args[4] +"', '" + entity.getReply().toString() +"')";
            else
                query = "insert into " + tableName +" values('"+ args[0] + "', '" + args[1] +"', '" + args[2] + "', '" + args[3] +"', '" + args[4] +"')";
            Statement stmt = con.createStatement();
            int x = stmt.executeUpdate(query);
            if (x == 0)
                throw new Exception("Insert Failed");
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public Message delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Message entity = findOne(id);

            String query = "DELETE from " + tableName +" WHERE id = " + id + ";";
            Statement stmt = con.createStatement();
            int x = stmt.executeUpdate(query);
            if (x > 0)
                return entity;
            else
                return null;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public Message update(Message entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if(findOne(entity.getId()) == null)
            return entity;
        return null;
    }

    @Override
    public Long size()
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            String query = "SELECT COUNT(*) AS total FROM " + tableName + ";";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int nr = 0;
            if(rs.next())
                nr = rs.getInt("total");
            return Long.valueOf(nr);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public Page<Message> findAll(Pageable pageable, Function<Message, Boolean> sortingFunction) {
        List<Message> list = new ArrayList<>();
        for(Message message:this.findAll())
            if (sortingFunction.apply(message))
                list.add(message);
        Paginator<Message> paginator = new Paginator<Message>(pageable, list);
        return paginator.paginate();
    }
}
