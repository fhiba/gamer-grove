package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityConstraint;

public class FollowCommunityForm {

    private int communityId;
    @ValidCommunityConstraint
    private String communityName;

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
