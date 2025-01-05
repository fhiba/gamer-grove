package ar.edu.itba.paw.webapp.dto;

public class ErrorValidationDTO {

    private String attribute;
    private String message;

    public static ErrorValidationDTO fromValidationError(String attribute, String message) {
        ErrorValidationDTO errorValidationDTO = new ErrorValidationDTO();
        errorValidationDTO.attribute = attribute;
        errorValidationDTO.message = message;
        return errorValidationDTO;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
