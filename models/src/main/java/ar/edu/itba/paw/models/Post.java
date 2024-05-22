package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_seq")
    @SequenceGenerator(sequenceName = "post_id_seq", name = "post_id_seq", allocationSize = 1)
    @Column(name ="id")
    private  long id;
    @Column(name = "title", nullable = false)
    private  String title;
    @Column(name = "body", nullable = false)
    private  String body;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @ManyToOne(targetEntity = Community.class)
    @JoinColumn(name = "community_name", referencedColumnName = "name")
    private Community community;

    @Column(name = "media", nullable = false)
    private  boolean media;

    @Column(name = "media_id")
    private  Long mediaId;

    @Column(name= "post_date", nullable = false)
    private  LocalDateTime date;
    @Column (name = "grooviness", nullable = false)
    private  int grooviness;
    @Column(name = "deleted", nullable = false)
    private  boolean deleted;

    @Column(name = "category")
    private String category;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_images", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "image_id")})
    private List<File> images;


    public Post(){
        //HIBERNATE ONLY
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public User getAuthor() {
        return author;
    }

    public Community getcommunity() {
        return community;
    }

    public boolean getMedia() {
        return media;
    }

    public long getMediaId() {
        return mediaId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getGrooviness() {
        return grooviness;
    }
    public String getCategory() {
        return category;
    }
    public Post(final long id, final String title, final String body, final User author, final Community community, final boolean media, final long media_id, final LocalDateTime date, final int grooviness, boolean deleted, final String category) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.community = community;
        this.media = media;
        this.mediaId = media_id;
        this.date = date;
        this.grooviness = grooviness;
        this.deleted = deleted;
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }


    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }

    public String getEncodedcommunity(){
        return URLEncoder.encode(community.getName(), StandardCharsets.UTF_8);
    }
}
