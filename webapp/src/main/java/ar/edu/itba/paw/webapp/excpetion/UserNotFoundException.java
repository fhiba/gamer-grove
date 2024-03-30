package ar.edu.itba.paw.webapp.excpetion;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
