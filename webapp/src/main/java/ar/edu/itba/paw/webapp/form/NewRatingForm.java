package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.*;

public class NewRatingForm {

    @Positive
    @NotNull
    private Long communityId;

    @NotNull
    @Min(value = 1)
    @Max(5)
    private Float rating;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
