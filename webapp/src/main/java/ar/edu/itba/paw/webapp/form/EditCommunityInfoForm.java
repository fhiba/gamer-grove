package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;

import javax.validation.constraints.NotBlank;

public class EditCommunityInfoForm {
    private final String description;
    private final String publisher;
    private final String developer;
    @ValidCommunityCategoriesConstraint
    private String categories;

    public EditCommunityInfoForm(String description, String publisher, String developer) {
        this.description = description;
        this.publisher = publisher;
        this.developer = developer;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDeveloper() {
        return developer;
    }
}
