package ar.edu.itba.paw.exceptions;

public class NoSuchGroovyCommentHistory extends StatusCodedException {
    public NoSuchGroovyCommentHistory() {
        super(StatusCodes.NOT_FOUND.getCode(), "Exception.NoSuchGroovyCommentHistory");
    }
}
