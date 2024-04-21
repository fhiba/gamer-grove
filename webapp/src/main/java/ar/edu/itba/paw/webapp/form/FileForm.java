package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.constraints.*;

public class FileForm {

    @NotNull
    @FileMustBeImageConstraint
    private MultipartFile file;

    @NotBlank
    private String communityName;


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}