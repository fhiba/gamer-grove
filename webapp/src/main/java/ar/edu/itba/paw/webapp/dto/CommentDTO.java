package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Comment;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

public class CommentDTO {

    private URI self;

    private URI post;

    private URI author;

    private Long id;

    private String body;

    private LocalDateTime date;

    private Integer grooviness;

    private Boolean deleted;

    public static Function<Comment, CommentDTO> mapper(UriInfo uriInfo) {
        return c -> fromComment(uriInfo, c);
    }

    public static CommentDTO fromComment(UriInfo uriInfo, Comment c) {
        final CommentDTO dto = new CommentDTO();
        dto.self = uriInfo.getBaseUriBuilder().path("posts").path(c.getPost().getId().toString()).path("comments")
                .path(String.valueOf(c.getId())).build();
        dto.author = uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(c.getAuthor().getId())).build();
        dto.post = uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(c.getPost().getId())).build();
        dto.body = c.getBody();
        dto.date = c.getDate();
        dto.id = c.getId();
        dto.deleted = c.getDeleted();
        dto.grooviness = c.getGrooviness();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPost() {
        return post;
    }

    public void setPost(URI post) {
        this.post = post;
    }

    public URI getAuthor() {
        return author;
    }

    public void setAuthor(URI author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
}
