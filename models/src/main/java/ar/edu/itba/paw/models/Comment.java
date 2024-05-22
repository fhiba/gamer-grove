package ar.edu.itba.paw.models;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name="comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_seq")
    @SequenceGenerator(name = "comment_id_seq",sequenceName = "comment_id_seq",allocationSize = 1)
    @Column(name = "id")
    private Long id;


    @ManyToOne
    private Post post;

    @ManyToOne
    private User author;

    @OneToOne(optional = false)
    private Comment parent;

    @Column(nullable = false,name = "body")
    private String body;

    @Column(nullable = false,name = "comment_date")
    private LocalDateTime date;

    @Column(nullable = false,name = "grooviness")
    private Integer grooviness;

    @Column(nullable = false,name = "deleted")
    private Boolean deleted;

    public Comment(){
        //For JPA
    }

    public Comment( Post post, User author, String body, LocalDateTime date, Integer grooviness, Boolean deleted) {
        this.post = post;
        this.author = author;
        this.parent = null;
        this.body = body;
        this.date = date;
        this.grooviness = grooviness;
        this.deleted = deleted;
    }

    public Comment( Post post,Comment parent, User author, String body, LocalDateTime date, Integer grooviness, Boolean deleted) {
        this.post = post;
        this.author = author;
        this.parent = parent;
        this.grooviness = grooviness;
        this.deleted = deleted;
        this.body = body;
        this.date = date;
    }

    public Long getId() {
        return id;
    }


    public String getBody() {
        return body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Integer getGrooviness() {
        return grooviness;
    }
    public Boolean isDeleted() {
        return deleted;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setGrooviness(Integer grooviness) {
        this.grooviness = grooviness;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getUsername() {
        return author.getUsername();
    }

    public Long getPostId() {
        return post.getId();
    }
}
