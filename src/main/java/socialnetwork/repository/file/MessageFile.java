package socialnetwork.repository.file;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageFile extends AbstractFileRepository<Long, Message> {

    public MessageFile(String fileName, Validator<Message> validator) {
        super(fileName, validator);
    }

    @Override
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
        if(attributes.size() == 5)
            id_reply = null;
        else
            id_reply = Long.parseLong(attributes.get(5));
        Message message = new Message(from, to, attributes.get(1), id_reply);
        message.setId(Long.parseLong(attributes.get(0)));
        message.setDate(attributes.get(2));
        return message;
    }

    @Override
    protected String createEntityAsString(Message entity) {
        return entity.getId() + ";" + entity.getMessage() + ";" + entity.getDate() + ";" + entity.getFrom() + ";" + entity.getTo().stream().map(Objects::toString).collect(Collectors.joining(","));
    }

    @Override
    protected void writeAllToFile(Iterable<Message> entities){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,false))) {
            for(Message entity: entities) {
                if (entity.getReply() == null)
                {
                    bW.write(createEntityAsString(entity));
                }
                else
                {
                    bW.write(createEntityAsString(entity) + ";" + entity.getReply());
                }
                bW.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void writeToFile(Message entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            if (entity.getReply() == null)
            {
                bW.write(createEntityAsString(entity));
            }
            else
            {
                bW.write(createEntityAsString(entity) + ";" + entity.getReply());
            }
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
