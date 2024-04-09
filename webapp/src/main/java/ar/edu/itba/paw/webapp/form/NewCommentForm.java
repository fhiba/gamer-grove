package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotBlank;

public class NewCommentForm {
    @NotBlank
    private String body;

    private long postId;
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
