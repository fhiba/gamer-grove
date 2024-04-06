package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidPostCategoryConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewPostForm {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @Size(min = 1)
    private String body;

    private String community;

    @NotBlank
    @ValidPostCategoryConstraint
    private String category;

    public String getTitle() {
        return title;
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

    public String getCategory() {
        return category;
    }

}
