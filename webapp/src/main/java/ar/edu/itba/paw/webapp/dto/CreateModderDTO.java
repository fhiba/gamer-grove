package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.webapp.validators.interfaces.CommunityMustExistConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UsernameMustExistConstraint;

public class CreateModderDTO {

    @NotBlank(message = "{NotBlank}")
    @Size(min = 4, max = 50, message = "{Size}")
    @UsernameMustExistConstraint(message = "{UsernameMustExist}")
    private String username;

    @NotNull(message = "{NotNull}")
    @CommunityMustExistConstraint(message = "{CommunityMustExist}")
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
