package ar.edu.itba.paw.exceptions;

public class UserNotFoundException extends StatusCodedException {

    public UserNotFoundException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.UserNotFound");
    }
}
