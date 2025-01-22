package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public class GiveRatingDTO {

    @NotNull(message = "{NotNull}")
    @Range(min = 1, max = 5, message = "{Range.rating}")
    private Float rating;

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
