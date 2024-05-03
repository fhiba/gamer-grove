package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public class UserPfpForm {
    @NotNull
    @FileMustBeImageConstraint
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
