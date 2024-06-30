package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PostDTO {
    private URI self;
    private  String title;
    private  String body;


    private URI author;

    private URI community;

    private  Boolean media;



    private LocalDateTime date;

    private  Integer grooviness;

    private  Boolean deleted;

    private String category;

    private List<URI> images;
    public static Function<Post, PostDTO> mapper(UriInfo uriInfo) {
        return p -> fromPost(uriInfo, p);
    }

    public static PostDTO fromPost(UriInfo uriInfo, Post p) {
        final PostDTO dto = new PostDTO();
        dto.author = uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(p.getAuthor().getId())).build();
        dto.body = p.getBody();
        dto.date = p.getDate();
        dto.category = p.getCategory();
        dto.community = uriInfo.getBaseUriBuilder().path("communities").path(String.valueOf(p.getcommunity().getId())).build();
        dto.deleted = p.getDeleted();
        dto.grooviness = p.getGrooviness();
        dto.title = p.getTitle();
        dto.media = p.getMedia();
        dto.images = new ArrayList<>();
        for (File image : p.getImages()) {
            dto.images.add(uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(image.getImageId())).build());
        }

        dto.self = uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(p.getId())).build();
        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public URI getAuthor() {
        return author;
    }

    public void setAuthor(URI author) {
        this.author = author;
    }

    public URI getCommunity() {
        return community;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    public Boolean getMedia() {
        return media;
    }

    public void setMedia(Boolean media) {
        this.media = media;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getGrooviness() {
        return grooviness;
    }

    public void setGrooviness(Integer grooviness) {
        this.grooviness = grooviness;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<URI> getImages() {
        return images;
    }

    public void setImages(List<URI> images) {
        this.images = images;
    }
}
