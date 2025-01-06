package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;
import ar.edu.itba.paw.webapp.validators.interfaces.FieldsMustMatchConstraint;

@FieldsMustMatchConstraint(first = "password", second = "repeatPassword", message = "{FieldsMustMatch.password}")
public class ResetPasswordDTO {

    private String token;

    @NotBlank(message = "{NotBlank}")
    @Size(min = 6, max = 50, message = "{Size}")
    private String password;

    @NotBlank(message = "{NotBlank}")
    @Size(min = 6, max = 50, message = "{Size}")
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
