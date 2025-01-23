package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.*;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.function.Function;

public class RatingDTO {

    private URI user;

    private URI community;

    private Float rating;

    public static Function<Rating, RatingDTO> mapper(UriInfo uriInfo) {
        return r -> fromRating(uriInfo, r);
    }

    public static RatingDTO fromRating(UriInfo uriInfo, Rating r) {
        final RatingDTO dto = new RatingDTO();
        dto.user = uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(r.getUser().getId())).build();
        dto.community = uriInfo.getBaseUriBuilder().path("communities").path(String.valueOf(r.getCommunity().getName()))
                .build();
        dto.rating = r.getRating();
        return dto;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getCommunity() {
        return community;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
