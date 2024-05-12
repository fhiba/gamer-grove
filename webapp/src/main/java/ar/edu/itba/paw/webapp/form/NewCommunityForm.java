package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public class NewCommunityForm {


    @NullableImageConstraint
    private MultipartFile image;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_. -]*$")
    private String name;

    private String description;

    @ValidCommunityCategoriesConstraint
    private String categories;

    private String publisher;

    private String developer;

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


    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

}
