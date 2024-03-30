package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotBlank;

public class NewCommunityForm {

    @NotBlank
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
