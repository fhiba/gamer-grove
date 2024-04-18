package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;

import javax.validation.constraints.NotBlank;


public class NewCommunityForm {

    @NotBlank
    private String name;

    private String description;

    @ValidCommunityCategoriesConstraint
    private String categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }


}
