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
    private final long id;
    @Column(name = "title", nullable = false)
    private final String title;
    @Column(name = "body", nullable = false)
    private final String body;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private final User authorId;

    @ManyToOne(targetEntity = Community.class)
    @JoinColumn(name = "community_name", referencedColumnName = "name")
    private final String communityName;

    @Column(name = "media", nullable = false)
    private final boolean media;

    private final long mediaId;

    @Column(name= "post_date", nullable = false)
    private final LocalDateTime date;
    @Column (name = "grooviness", nullable = false)
    private final int grooviness;
    @Column(name = "deleted", nullable = false)
    private final boolean deleted;


    private final String category;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_images", joinColumns = {@JoinColumn(name = "post_id")}, inverseJoinColumns = {@JoinColumn(name = "image_id")})
    private List<File> images;

    
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getAuthorId() {
        return authorId;
    }

    public String getCommunityName() {
        return communityName;
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
    public Post(final long id, final String title, final String body, final long author_id, final String community_name, final boolean media, final long media_id, final LocalDateTime date, final int grooviness, boolean deleted, final String category) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.authorId = author_id;
        this.communityName = community_name;
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


    public List<Integer> getImages() {
        return images;
    }

    public void setImages(List<Integer> images) {
        this.images = images;
    }

    public String getEncodedCommunityName(){
        return URLEncoder.encode(communityName, StandardCharsets.UTF_8);
    }
}
