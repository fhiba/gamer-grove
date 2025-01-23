package ar.edu.itba.paw.exceptions;

public class CommentIsDeletedException extends StatusCodedException {
    public CommentIsDeletedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.CommentIsDeleted");
    }
}
