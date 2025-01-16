package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidCommunityCategoriesConstraint;

public class CommunityCreationDTO {

    private final static int MAX_FILE_SIZE = (int) 5 * 1000 * 1000;

    @Size(max = MAX_FILE_SIZE, message = "{FileSize}")
    @FormDataParam("image")
    private byte[] bytes;

    @FileMustBeImageConstraint(message = "{Image}")
    @FormDataParam("image")
    private FormDataBodyPart fileDetails;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_. -]*$")
    @FormDataParam("name")
    private String name;

    @FormDataParam("description")
    private String description;

    @ValidCommunityCategoriesConstraint
    @FormDataParam("categories")
    private String categories;
    @FormDataParam("publisher")
    private String publisher;
    @FormDataParam("developer")
    private String developer;

    public static int getMaxFileSize() {
        return MAX_FILE_SIZE;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public FormDataBodyPart getFileDetails() {
        return fileDetails;
    }

    public void setFileDetails(FormDataBodyPart fileDetails) {
        this.fileDetails = fileDetails;
    }

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

}
