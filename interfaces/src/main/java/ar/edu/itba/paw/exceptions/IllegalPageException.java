package ar.edu.itba.paw.exceptions;

public class IllegalPageException extends StatusCodedException {
    public IllegalPageException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.IllegalPage");
    }
}
