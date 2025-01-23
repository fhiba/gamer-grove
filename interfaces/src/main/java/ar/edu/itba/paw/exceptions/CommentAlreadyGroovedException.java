package ar.edu.itba.paw.exceptions;

public class CommentAlreadyGroovedException extends StatusCodedException {

    public CommentAlreadyGroovedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.AlreadyGroovedComment");
    }
}
