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
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id", referencedColumnName = "id")
    private File portrait;

    @ElementCollection(targetClass = CommunityCategories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "communities_categories", joinColumns = @JoinColumn(name = "community_id", nullable = false))
    private List<CommunityCategories> categories;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "developer")
    private String developer;
    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "community_user", joinColumns = @JoinColumn(name = "community_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> followers;

    public Community() {
        //FOR HIBERNATE JPA
    }

    public Community( final String name, final String description, String publisher, String developer, LocalDateTime releaseDate) {

        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }
    public Community( final String name, final String description, List<CommunityCategories> categories, String publisher, String developer, LocalDateTime releaseDate) {

        this.name = name;
        this.description = description;
        this.categories = categories;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public File getPortrait_id() {
        return portrait;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCategories() {
        List<String> categories = null;
        for(CommunityCategories category : this.categories){
            categories.add(category.toString());
        }
        return categories;
    }

    public void setPortrait_id(File portrait) {
        this.portrait = portrait;
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
    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }
}
