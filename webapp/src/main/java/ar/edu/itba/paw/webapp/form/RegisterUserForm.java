package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsMustMatchConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueEmailConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldsMustMatchConstraint(first = "password", second = "repeatPassword")
public class RegisterUserForm {

    @NotBlank
    @Size(min = 4, max = 50)
    @UniqueUsernameConstraint
    private String username;
    @NotBlank
    @Email
    @UniqueEmailConstraint
    private String email;
    @NotBlank
    @Size(min = 6, max =  50)
    private String password;

    @NotBlank
    @Size(min = 5, max = 50)
    private String repeatPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

}
