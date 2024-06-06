package ar.edu.itba.paw.exceptions;

public class NoLoggedUserException extends Exception{
    public NoLoggedUserException() {
        super("No logged user");
    }
}
