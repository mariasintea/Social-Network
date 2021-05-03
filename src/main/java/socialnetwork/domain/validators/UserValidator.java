package socialnetwork.domain.validators;

import socialnetwork.domain.User;

import java.io.File;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().isEmpty() || !(entity.getFirstName().charAt(0)>='A' && entity.getFirstName().charAt(0)<='Z') || !entity.getFirstName().chars().allMatch(Character::isLetter))
            throw new ValidationException("First name is not valid!");

        if(entity.getLastName().isEmpty() || !(entity.getLastName().charAt(0)>='A' && entity.getLastName().charAt(0)<='Z') || !entity.getLastName().chars().allMatch(Character::isLetter))
            throw new ValidationException("Last name is not valid!");
        if(entity.getUsername().isEmpty())
            throw  new ValidationException("User name is not valid!");

        if(entity.getPassword().isEmpty())
            throw new ValidationException("Password is not valid!");

        if(!new File(entity.getPicture()).exists())
            throw new ValidationException("Path to picture is not valid!");
    }
}
