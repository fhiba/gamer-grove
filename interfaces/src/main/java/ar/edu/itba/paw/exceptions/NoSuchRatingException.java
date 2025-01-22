package ar.edu.itba.paw.exceptions;

public class NoSuchRatingException extends StatusCodedException {
    public NoSuchRatingException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.NoSuchRating");
    }
}
