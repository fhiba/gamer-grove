package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.UsernameMustExistConstraint;

import javax.validation.constraints.*;

public class NewModForm {

    @NotBlank
    @Size(min = 4, max = 50)
    @UsernameMustExistConstraint
    private String username;

    @NotNull
    private Long communityId;



    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
