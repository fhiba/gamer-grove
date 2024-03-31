package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidPostCategoryConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewPostForm {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @Size(min = 1, max = 500)
    private String body;

    private String community;

    @NotBlank
    @ValidPostCategoryConstraint
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
