package ar.edu.itba.paw.exceptions;

public class CommunityNotFollowedException extends StatusCodedException {
    public CommunityNotFollowedException() {
        super(StatusCodes.BAD_REQUEST.getCode(), "Exception.CommunityNotFollowed");
    }
}
