package exceptions;


public class EntityIsNotValidException extends RuntimeException {

    public EntityIsNotValidException(String message) {
        super(message);
    }

}