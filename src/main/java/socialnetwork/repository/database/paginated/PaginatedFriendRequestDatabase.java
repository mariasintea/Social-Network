package socialnetwork.repository.database.paginated;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Tuple;
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
import java.util.function.Function;

public class PaginatedFriendRequestDatabase implements PaginatedRepository<Tuple<Long, Long>, FriendshipRequest> {

    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<FriendshipRequest> validator;

    public PaginatedFriendRequestDatabase(String tableName, Validator<FriendshipRequest> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type FriendshipRequest having a specified list of @code attributes
     * @param attributes
     * @return an entity of type FriendshipRequest
     */
    public FriendshipRequest extractEntity(List<String> attributes)
    {
        FriendshipRequest friendshipRequest = new FriendshipRequest();
        friendshipRequest.setId(new Tuple<Long, Long>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));
        friendshipRequest.setDate(attributes.get(2));
        friendshipRequest.setStatus(attributes.get(3));
        return friendshipRequest;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(FriendshipRequest entity) {
        return entity.getId().toString() + ";" + entity.getDate().toString() + ";" + entity.getStatus();
    }

    @Override
    public FriendshipRequest findOne(Tuple<Long, Long> id){
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement();
            String query = "SELECT * FROM " + tableName + " WHERE id1 = " + id.getLeft() + " AND id2 = " + id.getRight() + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                List<String> attr = new ArrayList<>();
                attr.add(rs.getString(1));
                attr.add(rs.getString(2));
                attr.add(rs.getString(3));
                attr.add(rs.getString(4));
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
    public Iterable<FriendshipRequest> findAll() {
        List<FriendshipRequest> list = new ArrayList<>();
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
                    attr.add(rs.getString(1));
                    attr.add(rs.getString(2));
                    attr.add(rs.getString(3));
                    attr.add(rs.getString(4));
                    FriendshipRequest e = extractEntity(attr);
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
    public void addAll(Iterable<FriendshipRequest> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(FriendshipRequest entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "insert into " + tableName + " values('"+ args[0] + "', '" + args[1] +"', '" + args[2] +"', '" + args[3] +"')";
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
    public FriendshipRequest save(FriendshipRequest entity) {
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
            String query = "insert into " + tableName +" values('"+ args[0] + "', '" + args[1] +"', '" + args[2] + "', '" + args[3] +"')";
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
    public FriendshipRequest delete(Tuple<Long, Long> id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            FriendshipRequest entity = findOne(id);

            String query = "DELETE from " + tableName +" WHERE id1 = " + id.getLeft() + " AND id2 = " + id.getRight() + ";";
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
    public FriendshipRequest update(FriendshipRequest entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            String query = "UPDATE " + tableName + " SET id1 = " + entity.getId().getLeft() + ", id2 = " + entity.getId().getRight() + ", date = '" + entity.getDate() + "', status = '" + entity.getStatus() + "' WHERE id1 = " + entity.getId().getLeft() + " AND id2 = " + entity.getId().getRight() + ";";
            Statement stmt = con.createStatement();
            int x = stmt.executeUpdate(query);
            if (x > 0)
                return null;
            else
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
    public Page<FriendshipRequest> findAll(Pageable pageable, Function<FriendshipRequest, Boolean> sortingFunction) {
        List<FriendshipRequest> list = new ArrayList<>();
        for(FriendshipRequest request:this.findAll())
            if (sortingFunction.apply(request))
                list.add(request);
        Paginator<FriendshipRequest> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }
}
