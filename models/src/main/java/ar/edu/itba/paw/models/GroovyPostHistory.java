package ar.edu.itba.paw.models;

public class GroovyPostHistory {

    private int userId;
    private int postId;
    private boolean groovyType;


    public GroovyPostHistory(int userId, int postId, boolean groovyType) {
        this.userId = userId;
        this.postId = postId;
        this.groovyType = groovyType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public boolean isGroovyType() {
        return groovyType;
    }

    public void setGroovyType(boolean groovyType) {
        this.groovyType = groovyType;
    }
}
