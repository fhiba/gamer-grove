package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.models.Rating;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class GroovyCommentHistoryDTO {
    private URI user;

    private URI comment;

    private URI post;

    private int groovy;

    public static Function<GroovyCommentHistory, GroovyCommentHistoryDTO> mapper(UriInfo uriInfo) {
        return g -> fromRating(uriInfo, g);
    }

    public static GroovyCommentHistoryDTO fromRating(UriInfo uriInfo, GroovyCommentHistory g) {
        final GroovyCommentHistoryDTO dto = new GroovyCommentHistoryDTO();
        dto.user = uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(g.getUser().getId())).build();
        dto.comment = uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(g.getUser().getId()))
                .path("comments").path(String.valueOf(g.getUser().getId())).build();
        dto.post = uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(g.getUser().getId())).build();
        dto.groovy = g.isGroovy() ? GroovyEnum.UP.getValue() : GroovyEnum.DOWN.getValue();
        return dto;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getComment() {
        return comment;
    }

    public void setComment(URI comment) {
        this.comment = comment;
    }

    public URI getPost() {
        return post;
    }

    public void setPost(URI post) {
        this.post = post;
    }

    public int getGroovy() {
        return groovy;
    }

    public void setGroovy(int groovy) {
        this.groovy = groovy;
    }
}
