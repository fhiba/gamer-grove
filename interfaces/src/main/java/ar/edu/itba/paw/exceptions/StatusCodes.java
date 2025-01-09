package ar.edu.itba.paw.exceptions;

public enum StatusCodes {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    StatusCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StatusCodes fromCode(int code) {
        for (StatusCodes status : StatusCodes.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
