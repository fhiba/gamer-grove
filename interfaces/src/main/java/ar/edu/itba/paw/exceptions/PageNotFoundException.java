package ar.edu.itba.paw.exceptions;

public class PageNotFoundException extends StatusCodedException {
    public PageNotFoundException() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.PageNotFound");
    }
}
