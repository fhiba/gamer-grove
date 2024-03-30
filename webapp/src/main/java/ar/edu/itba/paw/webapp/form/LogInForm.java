package ar.edu.itba.paw.webapp.form;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class LogInForm {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

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
}
