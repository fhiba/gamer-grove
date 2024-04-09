package ar.edu.itba.paw.exceptions;

public class NoLoggedUserException extends Exception{
    public NoLoggedUserException(String message) {
        super(message);
    }
}
