package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "groovy_post_history")
@IdClass(GroovyPostHistory.GroovyPostHistoryKey.class)
public class GroovyPostHistory {

    @Id
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @OneToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;
    @Column(name="groovy_type")
    private Boolean groovyType;


    public GroovyPostHistory(User user, Post post, boolean groovyType) {
        this.user = user;
        this.post = post;
        this.groovyType = groovyType;
    }

    public GroovyPostHistory(){
        //HIBERNATE ONLY
    }

    public User getUserId() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public Post getPostId() {
        return post;
    }

    public void setPostId(Post postId) {
        this.post = post;
    }

    public Boolean isGroovyType() {
        return groovyType;
    }

    public void setGroovyType(Boolean groovyType) {
        this.groovyType = groovyType;
    }

    public static class GroovyPostHistoryKey implements Serializable{
        private User user;
        private Post post;

        public GroovyPostHistoryKey(User user, Post post) {
            this.user = user;
            this.post = post;
        }

        public GroovyPostHistoryKey(){
            //HIBERNATE ONLY
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GroovyPostHistoryKey that = (GroovyPostHistoryKey) o;

            if (user != null ? !user.equals(that.user) : that.user != null) return false;
            return post != null ? post.equals(that.post) : that.post == null;
        }

        @Override
        public int hashCode() {
            int result = user != null ? user.hashCode() : 0;
            result = 31 * result + (post != null ? post.hashCode() : 0);
            return result;
        }
    }
}
