package ar.edu.itba.paw.exceptions;

public class NoSuchTokenException extends StatusCodedException {

    public NoSuchTokenException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.NoSuchToken");

    }
}
