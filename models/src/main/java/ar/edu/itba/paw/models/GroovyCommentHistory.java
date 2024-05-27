package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "groovy_comment_history")
@IdClass(GroovyCommentHistory.GroovyCommentHistoryId.class)
public class GroovyCommentHistory {

    @Id
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @OneToOne(targetEntity = Comment.class)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    @Id
    @OneToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Column(name="groovy_type")
    private  Boolean groovy;

    public GroovyCommentHistory(User user, Comment comment, Post post, Boolean grooviness) {
        this.user = user;
        this.comment = comment;
        this.post = post;
        this.groovy = grooviness;
    }

    public GroovyCommentHistory() {

    }

    public User getUser() {
        return user;
    }

    public Comment getComment() {
        return comment;
    }

    public Boolean isGroovy() {
        return groovy;
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

    public void setGroovy(boolean grooviness) {
        this.groovy = grooviness;
    }

    @Embeddable
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
            return Objects.equals(user.getId(), that.user.getId()) && Objects.equals(comment.getId(), that.comment.getId()) && Objects.equals(post.getId(), that.post.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(user.getId(), comment.getId(), post.getId());
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
