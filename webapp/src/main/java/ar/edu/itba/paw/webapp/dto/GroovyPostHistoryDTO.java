package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.GroovyPostHistory;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class GroovyPostHistoryDTO {
    private URI user;
    private URI post;

    private  Boolean groovy;


    public static Function<GroovyPostHistory, GroovyPostHistoryDTO> mapper(UriInfo uriInfo) {
        return g -> fromRating(uriInfo, g);
    }

    public static GroovyPostHistoryDTO fromRating(UriInfo uriInfo, GroovyPostHistory g) {
        final GroovyPostHistoryDTO dto = new GroovyPostHistoryDTO();
        dto.user = uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(g.getUserId())).build();
        dto.post = uriInfo.getBaseUriBuilder().path("posts").path(String.valueOf(g.getPostId())).build();
        dto.groovy = g.isGroovyType();
        return dto;
    }
}
