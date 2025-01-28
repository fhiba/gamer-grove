package ar.edu.itba.paw.webapp.dto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.function.Function;

import javax.ws.rs.core.UriInfo;

import ar.edu.itba.paw.models.Mod;

public class ModderDTO {
    private URI user;
    private URI community;
    private URI self;
    private LocalDateTime sinceDate;

    public static Function<Mod, ModderDTO> mapper(UriInfo uriInfo) {
        return m -> fromModder(uriInfo, m);
    }

    public static ModderDTO fromModder(UriInfo uriInfo, Mod m) {
        final ModderDTO dto = new ModderDTO();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(m.getUser().getId())).build();
        dto.community = uriInfo.getBaseUriBuilder()
                .path("communities").path(m.getCommunity().getName())
                .build();
        dto.sinceDate = m.getSinceDate();
        dto.self = uriInfo.getBaseUriBuilder().path("mods")
                .path(m.getCommunity().getName())
                .path(String.valueOf(m.getUser().getId())).build();
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

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(LocalDateTime sinceDate) {
        this.sinceDate = sinceDate;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
