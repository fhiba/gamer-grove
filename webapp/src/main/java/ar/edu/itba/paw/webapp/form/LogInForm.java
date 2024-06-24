package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LogInForm {
    @NotBlank
    @Size(min = 4, max = 50)
    @UniqueUsernameConstraint
    private String username;
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
