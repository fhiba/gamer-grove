package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Email;

public class EmailForm {

    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
