package ar.edu.itba.paw.exceptions;

public class NotRatedCommunityException extends StatusCodedException {
    public NotRatedCommunityException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.NotRatedCommunity");
    }
}
