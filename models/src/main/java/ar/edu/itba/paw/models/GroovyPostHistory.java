package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "groovy_post_history")
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
}
