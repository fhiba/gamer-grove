package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class UserDTO {
    private  String username;
    private  String password;
    private String email;
    private  Boolean verified;
    private String locale;

    private  Boolean owner;

    private URI profileImage;
    private URI self;


    public static Function<User, UserDTO> mapper(UriInfo uriInfo) {
        return u -> fromUser(uriInfo, u);
    }

    public static UserDTO fromUser(UriInfo uriInfo, User u) {
        final UserDTO dto = new UserDTO();
        dto.username = u.getUsername();
        dto.email = u.getEmail();
        dto.owner = u.getOwner();
        dto.verified = u.getVerified();
        dto.locale = u.getLocale();
        dto.profileImage = uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(u.getPortraid_id())).build();
        dto.self = uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(u.getId())).build();
        return dto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getOwner() {
        return owner;
    }

    public void setOwner(Boolean owner) {
        this.owner = owner;
    }

    public URI getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(URI profileImage) {
        this.profileImage = profileImage;
    }
}
