package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.Email;

public class EmailDTO {
    @Email(message = "{Email}")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
