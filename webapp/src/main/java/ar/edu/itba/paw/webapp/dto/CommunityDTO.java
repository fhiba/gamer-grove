package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.*;

import javax.persistence.*;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CommunityDTO {
    private URI self;

    private String name;

    private String description;

    private URI portrait;

    private List<CommunityCategories> category;
    private String publisher;

    private String developer;

    private LocalDateTime releaseDate;

    private Float totalRating;

    private Integer ratingCount;

    public static Function<Community, CommunityDTO> mapper(UriInfo uriInfo) {
        return c -> fromCommunity(uriInfo, c);
    }

    public static CommunityDTO fromCommunity(UriInfo uriInfo, Community c) {
        final CommunityDTO dto = new CommunityDTO();
        dto.self = uriInfo.getBaseUriBuilder().path("communities").path(c.getName()).build();
        dto.category = c.getCategoriesEnum();
        dto.description = c.getDescription();
        dto.name = c.getName();
        dto.developer = c.getDeveloper();
        dto.releaseDate = c.getReleaseDate();
        dto.ratingCount = c.getRatingCount();
        dto.publisher = c.getPublisher();
        if (c.getPortrait() != null) {
            dto.portrait = uriInfo.getBaseUriBuilder().path("/api/images")
                    .path(String.valueOf(c.getPortrait().getImageId()))
                    .build();
        }
        dto.totalRating = c.getTotalRating();
        ;
        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
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

    public URI getPortrait() {
        return portrait;
    }

    public void setPortrait(URI portrait) {
        this.portrait = portrait;
    }

    public List<CommunityCategories> getCategory() {
        return category;
    }

    public void setCategory(List<CommunityCategories> category) {
        this.category = category;
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

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Float getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Float totalRating) {
        this.totalRating = totalRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
}
