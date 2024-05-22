package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "groovy_comment_history")
@IdClass(GroovyCommentHistory.GroovyCommentHistoryId.class)
public class GroovyCommentHistory {

    @Id
    @JoinColumn(name = "user_id")
    @OneToOne(targetEntity = User.class)
    private User user;

    @Id
    @JoinColumn(name = "comment_id")
    @OneToOne(targetEntity = Comment.class)
    private Comment comment;

    @Id
    @JoinColumn(name = "post_id")
    @OneToOne(targetEntity = Post.class)
    private Post post;
    @Column(name="groovy_type")
    private  Boolean grooviness;

    public GroovyCommentHistory(User user, Comment comment, Post post, boolean grooviness) {
        this.user = user;
        this.comment = comment;
        this.post = post;
        this.grooviness = grooviness;
    }

    public GroovyCommentHistory() {

    }


    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public boolean isGrooviness() {
        return grooviness;
    }

    public Post getPost() {
        return post;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public void setCommentId(Comment comment) {
        this.comment = comment;
    }

    public void setPostId(Post post) {
        this.post = post;
    }

    public void setGrooviness(boolean grooviness) {
        this.grooviness = grooviness;
    }

    public static class GroovyCommentHistoryId implements Serializable {
        private User user;

        private Comment comment;

        private Post post;

        public GroovyCommentHistoryId(User user, Comment comment, Post post) {
            this.user = user;
            this.comment = comment;
            this.post = post;
        }

        public GroovyCommentHistoryId() {
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroovyCommentHistoryId that = (GroovyCommentHistoryId) o;
            return Objects.equals(user, that.user) && Objects.equals(comment, that.comment) && Objects.equals(post, that.post);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, comment, post);
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }
    }
}
