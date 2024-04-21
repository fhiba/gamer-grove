package ar.edu.itba.paw.models;

import java.util.List;

public class Community {

    private final long id;
    private final String name;
    private final String description;
    private long portrait_id = -1;
    private List<String> categories = null;

    public Community(final long id, final String name,  final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public Community(final long id, final String name,  final String description, List<String> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categories = categories;
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
}
