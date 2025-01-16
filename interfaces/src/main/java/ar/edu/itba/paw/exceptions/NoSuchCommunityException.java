package ar.edu.itba.paw.exceptions;

public class NoSuchCommunityException extends StatusCodedException {

    public NoSuchCommunityException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.CommunityNotFound");
    }
}
