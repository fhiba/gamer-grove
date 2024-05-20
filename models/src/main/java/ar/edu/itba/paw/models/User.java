package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Column(nullable = false,unique = true, name = "username")
    private  String username;
    @Column(nullable = false, name = "password")
    private  String password;
    @Column(nullable = false, unique = true, name="email")
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
    @Column(name ="id")
    private long id;
    @Column(name= "verified", nullable = false)
    private  Boolean verified;
    @Column(name="portrait_id")
    private  long portraid_id;
    @Column(name = "locale", nullable = false)
    private String locale;
    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public User() {
        //FOR HIBERNATE JPA
    }

    public User(final long id, final String username, final String password, final String email, long portraidId, Boolean verified) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.password = password;
        portraid_id = portraidId;
        this.verified = verified;
        this.locale = "es";
    }

    public User(final long id,final String username, final String password, final String email, long portraidId, Boolean verified, String locale) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.password = password;
        portraid_id = portraidId;
        this.verified = verified;
        this.locale = locale;
    }

    public Boolean isVerified() {
        return verified;
    }

    public long getPortraid_id() {
        return portraid_id;
    }

    public String getLocale() {
        return locale;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public void setPortraid_id(long portraid_id) {
        this.portraid_id = portraid_id;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
