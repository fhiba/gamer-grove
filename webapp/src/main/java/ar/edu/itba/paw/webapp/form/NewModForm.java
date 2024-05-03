package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.UsernameMustExistConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityConstraint;

import javax.validation.constraints.*;

public class NewModForm {

    @NotBlank
    @Size(min = 4, max = 50)
    @UsernameMustExistConstraint
    private String username;

    @NotBlank
    @Email
    private String email;
    @NotNull
    private Long communityId;

    public String getEmail() {
        return email;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
