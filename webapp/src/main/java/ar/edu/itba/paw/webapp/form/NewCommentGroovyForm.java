package ar.edu.itba.paw.webapp.form;



public class NewCommentGroovyForm {


    private int commentId;

    private int commentPostId;

    private boolean groovyType;


    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public boolean isGroovyType() {
        return groovyType;
    }

    public void setGroovyType(boolean groovyType) {
        this.groovyType = groovyType;
    }

    public int getCommentPostId() {
        return commentPostId;
    }

    public void setCommentPostId(int commentPostId) {
        this.commentPostId = commentPostId;
    }
}
