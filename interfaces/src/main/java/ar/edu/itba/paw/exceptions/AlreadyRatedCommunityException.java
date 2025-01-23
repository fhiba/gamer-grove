package ar.edu.itba.paw.exceptions;

public class AlreadyRatedCommunityException extends StatusCodedException {
    public AlreadyRatedCommunityException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.AlreadyRatedCommunity");
    }
}
