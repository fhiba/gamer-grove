package ar.edu.itba.paw.exceptions;

public class StatusCodedRuntimeException
        extends RuntimeException {

    private final int statusCode;
    private final String messageCode;

    public StatusCodedRuntimeException(int statusCode, String messageCode) {
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
