package ar.edu.itba.paw.models;

public class GroovyCommentHistory {

    private final int userId;
    private final int commentId;
    private final int postId;
    private final boolean grooviness;

    public GroovyCommentHistory(int userId, int commentId, int postId, boolean grooviness) {
        this.userId = userId;
        this.commentId = commentId;
        this.postId = postId;
        this.grooviness = grooviness;
    }


    public int getUserId() {
        return userId;
    }

    public int getCommentId() {
        return commentId;
    }

    public boolean isGrooviness() {
        return grooviness;
    }

    public int getPostId() {
        return postId;
    }
}
