package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsMustMatchConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsMustMatchConstraint(first = "password", second = "repeatPassword")
public class ResetPasswordForm {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max =  50)
    private String password;

    @NotBlank
    @Size(min = 6, max = 50)
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
