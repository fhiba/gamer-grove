package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class RemoveModForm {
    @NotEmpty
    @Size(min = 4, max = 50)
    private String removeUsername;
    @NotNull
    private Long fromCommunityId;

    public String getRemoveUsername() {
        return removeUsername;
    }

    public void setRemoveUsername(String removeUsername) {
        this.removeUsername = removeUsername;
    }

    public Long getFromCommunityId() {
        return fromCommunityId;
    }

    public void setFromCommunityId(Long fromCommunityId) {
        this.fromCommunityId = fromCommunityId;
    }
}
