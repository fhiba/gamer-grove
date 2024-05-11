package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FileMustBeImageConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.NullableImageConstraint;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserPfpForm {
    @NotNull
    @NullableImageConstraint
    private MultipartFile file;

    @NotBlank
    private String locale;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
