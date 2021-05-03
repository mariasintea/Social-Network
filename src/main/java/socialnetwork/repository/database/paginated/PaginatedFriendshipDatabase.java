package socialnetwork.repository.database.paginated;

import socialnetwork.domain.Friendship;
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

public class PaginatedFriendshipDatabase implements PaginatedRepository<Tuple<Long, Long>, Friendship> {

    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<Friendship> validator;

    public PaginatedFriendshipDatabase(String tableName, Validator<Friendship> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type Friendship having a specified list of @code attributes
     * @param attributes
     * @return an entity of type Friendship
     */
    public Friendship extractEntity(List<String> attributes) {
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<Long, Long>(Long.parseLong(attributes.get(0)), Long.parseLong(attributes.get(1))));
        friendship.setDate(attributes.get(2));
        return friendship;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().toString() + ";" + entity.getDate();
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id){
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Statement stmt = con.createStatement();
            String query = "SELECT * FROM " + tableName + " WHERE id_friend1 = " + id.getLeft() + "AND id_friend2 = " + id.getRight() + ";";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
            {
                List<String> attr = new ArrayList<>();
                attr.add(rs.getString(1));
                attr.add(rs.getString(2));
                attr.add(rs.getString(3));
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
    public Iterable<Friendship> findAll() {
        List<Friendship> list = new ArrayList<>();
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
                    Friendship e = extractEntity(attr);
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
    public void addAll(Iterable<Friendship> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(Friendship entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "insert into " + tableName + " values('"+ args[0] + "', '" + args[1] +"', '" + args[2] +"')";
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
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        //System.out.println(findOne(entity.getId()).getId().toString());
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
            String query = "insert into " + tableName +" values('"+ args[0] + "', '" + args[1] +"', '" + args[2] +"')";
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
    public Friendship delete(Tuple<Long, Long> id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            Friendship entity = findOne(id);

            String query = "DELETE from " + tableName +" WHERE id_friend1 = " + id.getLeft() + " AND id_friend2 = " + id.getRight() + ";";
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
    public Friendship update(Friendship entity) {

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
    public Page<Friendship> findAll(Pageable pageable, Function<Friendship, Boolean> sortingFunction) {
        List<Friendship> list = new ArrayList<>();
        for(Friendship friendship:this.findAll())
            if (sortingFunction.apply(friendship))
                list.add(friendship);
        Paginator<Friendship> paginator = new Paginator<>(pageable, list);
        return paginator.paginate();
    }
}
