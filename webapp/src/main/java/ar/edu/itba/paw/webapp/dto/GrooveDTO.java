package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotNull;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidGrooveValue;

public class GrooveDTO {

    @NotNull
    @ValidGrooveValue
    private int groovy;

    public GrooveDTO() {
    }

    public int getGroovy() {
        return groovy;
    }

    public void setGroovy(int groovy) {
        this.groovy = groovy;
    }

}
