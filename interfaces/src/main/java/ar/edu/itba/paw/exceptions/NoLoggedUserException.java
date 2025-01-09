package ar.edu.itba.paw.exceptions;

public class NoLoggedUserException extends StatusCodedRuntimeException {
    public NoLoggedUserException() {
        super(StatusCodes.UNAUTHORIZED.getCode(), "Exception.NoLogged");
    }
}
