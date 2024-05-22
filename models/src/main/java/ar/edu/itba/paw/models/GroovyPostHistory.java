package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "groovy_post_history")
public class GroovyPostHistory {

    @Id
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private int userId;

    @Id
    @OneToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private int postId;
    @Column(name="groovy_type")
    private boolean groovyType;


    public GroovyPostHistory(int userId, int postId, boolean groovyType) {
        this.userId = userId;
        this.postId = postId;
        this.groovyType = groovyType;
    }

    public GroovyPostHistory(){
        //HIBERNATE ONLY
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
