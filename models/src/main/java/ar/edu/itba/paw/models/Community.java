package ar.edu.itba.paw.models;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class Community {

    private final long id;
    private final String name;
    private final String description;
    private long portrait_id = -1;
    private List<String> categories = null;
    private final String publisher;
    private final String developer;
    private final LocalDateTime releaseDate;

    public Community(final long id, final String name, final String description, String publisher, String developer, LocalDateTime releaseDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.publisher = publisher;
        this.developer = developer;
        this.releaseDate = releaseDate;
    }
    public Community(final long id, final String name, final String description, List<String> categories, String publisher, String developer, LocalDateTime releaseDate) {
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

    public List<String> getCategories() {
        return categories;
    }

    public void setPortrait_id(long portrait_id) {
        this.portrait_id = portrait_id;
    }

    public void setCategories(List<String> categories) {
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
