package ar.edu.itba.paw.exceptions;

public class NoSuchCommentException extends StatusCodedException {

    public NoSuchCommentException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.CommentNotFound");
    }
}
