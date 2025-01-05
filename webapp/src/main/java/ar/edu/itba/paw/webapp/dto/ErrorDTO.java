package ar.edu.itba.paw.webapp.dto;

public class ErrorDTO {

    private String message;

    public static ErrorDTO fromErrorMsg(String message) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.message = message;
        return errorDTO;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
