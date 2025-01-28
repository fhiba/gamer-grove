package ar.edu.itba.paw.exceptions;

public class UserIsNotModException extends StatusCodedException {
    public UserIsNotModException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.UserIsNotMod");
    }

}
