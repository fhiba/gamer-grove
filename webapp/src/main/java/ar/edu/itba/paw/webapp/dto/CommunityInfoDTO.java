package ar.edu.itba.paw.webapp.dto;

import java.util.List;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesListConstraint;

public class CommunityInfoDTO {

    @ValidCommunityCategoriesListConstraint
    private List<String> categories;

    private String developer;

    private String publisher;

    private String description;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
