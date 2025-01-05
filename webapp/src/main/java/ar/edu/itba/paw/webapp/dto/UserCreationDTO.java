package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsMustMatchConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueEmailConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;

@FieldsMustMatchConstraint(first = "password", second = "repeatPassword", message = "{FieldsMustMatch}")
public class UserCreationDTO {

    @NotBlank(message = "{NotBlank}")
    @Size(min = 4, max = 50, message = "{Size}")
    @UniqueUsernameConstraint(message = "{UniqueUsername}")
    private String username;
    @NotBlank(message = "{NotBlank}")
    @Email(message = "{Email}")
    @UniqueEmailConstraint(message = "{UniqueEmail}")
    private String email;
    @NotBlank(message = "{NotBlank}")
    @Size(min = 6, max = 50, message = "{Size}")
    private String password;

    @NotBlank(message = "{NotBlank}")
    @Size(min = 6, max = 50, message = "{Size}")
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
