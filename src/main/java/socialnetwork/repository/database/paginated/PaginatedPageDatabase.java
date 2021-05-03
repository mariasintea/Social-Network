package socialnetwork.repository.database.paginated;

import socialnetwork.domain.Page;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
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

public class PaginatedPageDatabase implements PaginatedRepository<Long, Page> {

    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<Page> validator;

    public PaginatedPageDatabase(String tableName, Validator<Page> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type Page having a specified list of @code attributes
     * @param attributes
     * @return an entity of type Page
     */
    public Page extractEntity(List<String> attributes) {
        List<Tuple<Long, Long>> friendList = new ArrayList<>();
        String[] args;
        if(attributes.get(3) != null) {
            args = attributes.get(3).split(",");
            for (String string : args) {
                String[] a = string.split("-");
                Long id1 = Long.parseLong(a[0]);
                Long id2 = Long.parseLong(a[1]);
                friendList.add(new Tuple<>(id1, id2));
            }
        }
        List<Long> messageList = new ArrayList<Long>();
        if(attributes.get(4) != null) {
            args = attributes.get(4).split(",");
            for (String string : args) {
                Long id = Long.parseLong(string);
                messageList.add(id);
            }
        }
        List<Tuple<Long, Long>> requestList = new ArrayList<>();
        if(attributes.get(5) != null) {
            args = attributes.get(5).split(",");
            for (String string : args) {
                String[] a = string.split("-");
                Long id1 = Long.parseLong(a[0]);
                Long id2 = Long.parseLong(a[1]);
                requestList.add(new Tuple<>(id1, id2));
            }
        }
        Page page = new Page(attributes.get(1), attributes.get(2), friendList, messageList, requestList);
        page.setId(Long.parseLong(attributes.get(0)));
        return page;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(Page entity) {
        return entity.getId().toString() + ";" + entity.getName() + ";" + entity.getSurname() + ";" + entity.getFriendsList().stream().map(x->x.toString2()).collect(Collectors.joining(",")) + ";" + entity.getMessageList().stream().map(Objects::toString).collect(Collectors.joining(",")) + ";" + entity.getRequestList().stream().map(x->x.toString2()).collect(Collectors.joining(","));
    }

    @Override
    public Page findOne(Long id){
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
                attr.add(rs.getString(6));
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
    public Iterable<Page> findAll() {
        List<Page> list = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = "select * from " + tableName;
            ResultSet rs = stmt.executeQuery(query);
            rs.beforeFirst();
            while(!rs.isLast()) {
                if (rs.next()) {
                    List<String> attr = new ArrayList<>();
                    attr.add(rs.getString(6));
                    attr.add(rs.getString(1));
                    attr.add(rs.getString(2));
                    attr.add(rs.getString(3));
                    attr.add(rs.getString(4));
                    attr.add(rs.getString(5));
                    Page e = extractEntity(attr);
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
    public void addAll(Iterable<Page> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(Page entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "insert into " + tableName + " values('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "', '" + args[4]+ "', '" + args[5] + "')";
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
    public Page save(Page entity) {
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
            if(args.length < 4)
                query = "insert into " + tableName +" (id, name, surname) values('"+ args[0] + "', '" + args[1] +"', '" + args[2] + "')";
            else
                query = "insert into " + tableName +" values('"+ args[0] + "', '" + args[1] +"', '" + args[2] + "', '" + args[3] + "', '" + args[4]+ "', '" + args[5] + "')";
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
    public Page delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Page entity = findOne(id);

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
    public Page update(Page entity) {

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

            String friendsList = "'" + entity.getFriendsList().stream().map(x->x.toString2()).collect(Collectors.joining(",")) + "'";
            String messagesList = "'" + entity.getMessageList().stream().map(x->x.toString()).collect(Collectors.joining(",")) + "'";
            String requestList = "'" + entity.getRequestList().stream().map(x->x.toString2()).collect(Collectors.joining(",")) + "'";

            if(friendsList.equals("''"))
                friendsList = "NULL";
            if(messagesList.equals("''"))
                messagesList = "NULL";
            if(requestList.equals("''"))
                requestList = "NULL";
            String query = "UPDATE " + tableName + " SET friends = " + friendsList + ", messages = " + messagesList + ", requests = " + requestList + " WHERE id = " + entity.getId() + ";";
            Statement stmt = con.createStatement();
            int x = stmt.executeUpdate(query);
            if (x <= 0)
                return null;
            return entity;
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
    public socialnetwork.repository.paging.Page<Page> findAll(Pageable pageable, Function<Page, Boolean> sortingFunction) {
        List<Page> list = new ArrayList<>();
        for(Page page:this.findAll())
            if (sortingFunction.apply(page))
                list.add(page);
        Paginator<Page> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }
}

