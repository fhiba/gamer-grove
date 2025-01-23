package ar.edu.itba.paw.exceptions;

public class AlreadyFollowedException extends StatusCodedException {
    public AlreadyFollowedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.AlreadyFollowed");
    }
}
