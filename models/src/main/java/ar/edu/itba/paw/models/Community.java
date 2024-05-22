package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;



@Entity
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_id_seq")
    @SequenceGenerator(sequenceName = "community_id_seq", name = "community_id_seq", allocationSize = 1)
    @Column(name ="id")
    private long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "portrait_id")
    private long portrait_id = -1;

    @ElementCollection(targetClass = CommunityCategories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "communities_categories", joinColumns = @JoinColumn(name = "community_id", nullable = false))
    private List<CommunityCategories> categories = null;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "developer")
    private String developer;
    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    public Community() {
        //FOR HIBERNATE JPA
    }

    public Community(final long id, final String name, final String description, String publisher, String developer, LocalDateTime releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }
    public Community(final long id, final String name, final String description, List<CommunityCategories> categories, String publisher, String developer, LocalDateTime releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categories = categories;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPortrait_id() {
        return portrait_id;
    }

    public String getDescription() {
        return description;
    }

    public List<CommunityCategories> getCategories() {
        return categories;
    }

    public void setPortrait_id(long portrait_id) {
        this.portrait_id = portrait_id;
    }

    public void setCategories(List<CommunityCategories> categories) {
        this.categories = categories;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public String getEncodedName(){
        return URLEncoder.encode(name, StandardCharsets.UTF_8);
    }
}
