package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.FilesMustBeImagesConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class EditCommunityInfoForm {

    @NullableImageConstraint
    private MultipartFile image;

    @NotBlank
    private String description;

    private String publisher;

    private String developer;

    @ValidCommunityCategoriesConstraint
    private String categories;

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

}
