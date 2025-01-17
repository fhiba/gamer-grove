package ar.edu.itba.paw.exceptions;

public class PostAlreadyGroovedException extends StatusCodedException {
    public PostAlreadyGroovedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.PostAlreadyGrooved");
    }

}
