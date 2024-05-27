package ar.edu.itba.paw.models;



import javax.persistence.*;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Convert(converter = CommunityCategoryConverter.class)
    @ElementCollection(targetClass = CommunityCategories.class)
    @CollectionTable(name = "communities_categories", joinColumns = @JoinColumn(name = "community_id", nullable = false))
    private List<CommunityCategories> category;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "developer")
    private String developer;
    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @ManyToMany(mappedBy = "modderCommunities")
    private List<User> modders;

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
        this.category = categories;
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

    public List<String> getCategory() {
        List<String> categories = new ArrayList<>();
        for(CommunityCategories category : this.category){
                categories.add(category.getCategory());
        }
        return categories;
    }

    public List<CommunityCategories> getCategoriesEnum() {
        return category;
    }

    public void setPortrait(File portrait) {
        this.portrait = portrait;
    }

    public void setCategory(List<CommunityCategories> categories) {
        this.category = categories;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Community community = (Community) o;
        return Objects.equals(id, community.id) && Objects.equals(name, community.name) && Objects.equals(description, community.description) && Objects.equals(portrait, community.portrait) && Objects.equals(category, community.category) && Objects.equals(publisher, community.publisher) && Objects.equals(developer, community.developer) && Objects.equals(releaseDate, community.releaseDate) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public List<User> getModders() {
        return modders;
    }

    public void setModders(List<User> modders) {
        this.modders = modders;
    }
}
