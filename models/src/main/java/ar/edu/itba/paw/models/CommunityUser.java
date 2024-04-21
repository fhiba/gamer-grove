package ar.edu.itba.paw.models;

public class CommunityUser {
    private int communityId;
    private int userId;
    private int communityRole;
    private String communityName;

    public CommunityUser(int communityId, int userId, int communityRole, String communityName) {
        this.communityId = communityId;
        this.userId = userId;
        this.communityRole = communityRole;
        this.communityName = communityName;
    }

    public int getCommunityId() {
        return communityId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCommunityRole() {
        return communityRole;
    }

    public String getCommunityName() {
        return communityName;
    }
}
