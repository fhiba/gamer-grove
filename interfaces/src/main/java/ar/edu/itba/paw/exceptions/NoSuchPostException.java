package ar.edu.itba.paw.exceptions;

public class NoSuchPostException extends StatusCodedException {

    public NoSuchPostException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.NoSuchPost");
    }
}
