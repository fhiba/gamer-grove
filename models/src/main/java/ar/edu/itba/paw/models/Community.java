package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "community")
public class Community implements Serializable {

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

    public File getPortrait() {
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

    public void setPortrait(File portrait) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Community community = (Community) o;
        return Objects.equals(id, community.id) && Objects.equals(name, community.name) && Objects.equals(description, community.description) && Objects.equals(portrait, community.portrait) && Objects.equals(categories, community.categories) && Objects.equals(publisher, community.publisher) && Objects.equals(developer, community.developer) && Objects.equals(releaseDate, community.releaseDate) && Objects.equals(followers, community.followers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, portrait, categories, publisher, developer, releaseDate, followers);
    }
}
