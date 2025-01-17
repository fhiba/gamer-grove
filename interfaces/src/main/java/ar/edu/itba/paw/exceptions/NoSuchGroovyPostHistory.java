package ar.edu.itba.paw.exceptions;

public class NoSuchGroovyPostHistory extends StatusCodedException {
    public NoSuchGroovyPostHistory() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.NoSuchGroovyPostHistory");
    }
}
