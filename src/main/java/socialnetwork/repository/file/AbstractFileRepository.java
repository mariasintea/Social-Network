package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    /*
    * loads all entities from file
    */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while((line=br.readLine())!=null){
                List<String> attr=Arrays.asList(line.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     *  creates a String containing dates from entity
     * @param entity
     * @return a String containing dates from entity
     */
    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
        E e=super.save(entity);
        if (e==null)
        {
            writeToFile(entity);
        }
        return e;

    }

    @Override
    public E delete(ID id)
    {
        E e = super.delete(id);
        if (e != null)
        {
            writeAllToFile(super.findAll());
        }
        return e;
    }

    @Override
    public E update(E entity)
    {
        E e = super.update(entity);
        if (e == null)
        {
            writeAllToFile(super.findAll());
        }
        return e;
    }

    /**
     * writes all entities to file
     * @param entities - list of entities
     */
    protected void writeAllToFile(Iterable<E> entities){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,false))) {
            for(E entity: entities)
            {
                bW.write(createEntityAsString(entity));
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * writes entity to file
     * @param entity
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

