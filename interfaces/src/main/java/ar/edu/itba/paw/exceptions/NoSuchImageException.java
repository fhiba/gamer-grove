package ar.edu.itba.paw.exceptions;

public class NoSuchImageException extends StatusCodedException{
    public NoSuchImageException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.ImageNotFound");
    }
}
