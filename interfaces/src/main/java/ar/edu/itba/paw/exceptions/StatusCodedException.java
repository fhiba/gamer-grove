package ar.edu.itba.paw.exceptions;

public class StatusCodedException
        extends Exception {

    private final int statusCode;
    private final String messageCode;

    public StatusCodedException(int statusCode, String messageCode) {
        super();
        this.statusCode = statusCode;
        this.messageCode = messageCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
