package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase implements Repository<Long, User> {
    String tableName;
    final private String databaseName = "jdbc:postgresql://localhost:5432/socialnetwork";
    final private Validator<User> validator;

    public UserDatabase(String tableName, Validator<User> validator)
    {
        this.validator = validator;
        this.tableName = tableName;
    }

    /**
     * creates an entity of type User having a specified list of @code attributes
     * @param attributes
     * @return an entity of type User
     */
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5));
        user.setId(Long.parseLong(attributes.get(0)));
        return user;
    }

    /**
     * creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected String createEntityAsString(User entity) {
        return entity.getId().toString() + ";" + entity.getFirstName() + ";" + entity.getLastName() + ";" + entity.getUsername() + ";" + entity.getPassword() + ";" + entity.getPicture();
    }

    @Override
    public User findOne(Long id)
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
    public Iterable<User> findAll() {
        List<User> list = new ArrayList<>();
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
                    attr.add(rs.getString(6));
                    User e = extractEntity(attr);
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
    public void addAll(Iterable<User> list)
    {
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            for(User entity: list)
            {
                String row = createEntityAsString(entity);
                String[] args = row.split(";");
                String query = "INSERT INTO " + tableName + " VALUES('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "', '" + args[4] + "', '" + args[5] + "')";
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
    public User save(User entity) {
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
            String query = "INSERT INTO " + tableName +" VALUES('"+ args[0] + "', '" + args[1] + "', '" + args[2] + "', '" + args[3] + "', '" + args[4] + "', '" + args[5] +"')";
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
    public User delete(Long id) {
        if(id == null)
            throw new IllegalArgumentException("Non existent user!");

        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            User entity = findOne(id);

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
    public User update(User user)
    {
        if(user == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(user);

        if(findOne(user.getId()) == null)
            return user;
        try
        {
            Connection con = DriverManager.getConnection(databaseName, "postgres", "10110100");
            if (con == null)
                throw new Exception("Unable to connect to database!");

            String query = "UPDATE " + tableName + " SET name = '" + user.getFirstName() + "', surname = '" + user.getLastName() + "', username = '" + user.getUsername()  + "', password = '" + user.getPassword() + "', picture = '" + user.getPicture() + "' WHERE id = " + user.getId() + ";";
            Statement stmt = con.createStatement();
            int x = stmt.executeUpdate(query);
            if (x > 0)
                return user;
            else
                return null;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return user;
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
}
