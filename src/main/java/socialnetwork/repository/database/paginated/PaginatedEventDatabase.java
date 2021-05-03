package socialnetwork.repository.database.paginated;

import socialnetwork.domain.Event;
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

public class PaginatedEventDatabase implements PaginatedRepository<Long, Event> {
    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<Event> validator;

    public PaginatedEventDatabase(String tableName, Validator<Event> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type Event having a specified list of @code attributes
     * @param attributes
     * @return an entity of type Event
     */
    public Event extractEntity(List<String> attributes) {
        List<Long> subscribersList = new ArrayList<>();
        if(attributes.get(4) != null) {
            String[] args = attributes.get(4).split(",");
            for (String string : args)
                subscribersList.add(Long.parseLong(string));
        }
        Event event = new Event(Long.parseLong(attributes.get(1)),attributes.get(2), attributes.get(3), subscribersList);
        event.setId(Long.parseLong(attributes.get(0)));
        return event;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(Event entity) {
        return entity.getId().toString() + ";" + entity.getPage().toString() + ";" + entity.getName() + ";" + entity.getDate() + ";" + entity.getSubscribers().stream().map(Objects::toString).collect(Collectors.joining(","));
    }

    @Override
    public Event findOne(Long id)
    {
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
            if (rs.next())
            {
                List<String> attr = new ArrayList<>();
                attr.add(rs.getString(1));
                attr.add(rs.getString(2));
                attr.add(rs.getString(3));
                attr.add(rs.getString(4));
                attr.add(rs.getString(5));
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
    public Iterable<Event> findAll() {
        List<Event> list = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "SELECT * FROM " + tableName;
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
                    Event e = extractEntity(attr);
                    list.add(e);
                }
                else
                    return list;
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
    public void addAll(Iterable<Event> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(Event entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "INSERT INTO " + tableName + " VALUES('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "', '" + args[4] + "')";
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
    public Event save(Event entity) {
        if (entity==null)
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
            if(args.length < 5)
                query = "INSERT INTO " + tableName +"(id, page, name, date) VALUES('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "')";
            else
                query = "INSERT INTO " + tableName +" VALUES('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "', '" + args[4] + "')";
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
    public Event delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Event entity = findOne(id);

            String query = "DELETE FROM " + tableName +" WHERE id = " + id + ";";
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
    public Event update(Event entity)
    {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if(findOne(entity.getId()) == null)
            return entity;
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            String subscribersList = entity.getSubscribers().stream().map(Objects::toString).collect(Collectors.joining(","));
            String query;
            if (!subscribersList.equals(""))
                query = "UPDATE " + tableName + " SET subscribers = '" + subscribersList + "' WHERE id = '" + entity.getId() + "';";
            else
                query = "UPDATE " + tableName + " SET subscribers = NULL WHERE id = '" + entity.getId() + "';";
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
        return entity;
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
    public Page<Event> findAll(Pageable pageable, Function<Event, Boolean> sortingFunction) {
        List<Event> list = new ArrayList<>();
        for(Event event:this.findAll())
            if (sortingFunction.apply(event))
                list.add(event);
        Paginator<Event> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }
}

