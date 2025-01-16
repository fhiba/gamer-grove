package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.models.GroovyPostHistory;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.function.Function;

public class GroovyPostHistoryDTO {
    private URI user;
    private URI post;

    private Integer groovy;

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyPostHistoryDTO.class);

    public static Function<GroovyEnum, GroovyPostHistoryDTO> mapper(UriInfo uriInfo) {
        return g -> fromRating(uriInfo, g);
    }

    public static GroovyPostHistoryDTO fromRating(UriInfo uriInfo, GroovyEnum g) {
        final GroovyPostHistoryDTO dto = new GroovyPostHistoryDTO();
        LOGGER.info("path from uriInfo: {}", uriInfo.getRequestUri().getQuery());
        dto.groovy = g.getValue();
        return dto;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getPost() {
        return post;
    }

    public void setPost(URI post) {
        this.post = post;
    }

    public Integer getGroovy() {
        return groovy;
    }

    public void setGroovy(Integer groovy) {
        this.groovy = groovy;
    }
}
