package ar.edu.itba.paw.exceptions;

public class PostIsDeletedException extends StatusCodedException {
    public PostIsDeletedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.PostIsDeleted");
    }
}
