package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FilesMustBeImagesConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidPostCategoryConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class NewPostForm {


    @NotBlank
    @Size(min = 1, max = 150)
    private String title;

    @NotBlank
    @Size(min = 1)
    private String body;

    @NotBlank
    @ValidCommunityConstraint
    private String community;

    @NotBlank
    @ValidPostCategoryConstraint
    private String category;
    @FilesMustBeImagesConstraint
    private MultipartFile[] files;


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

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
