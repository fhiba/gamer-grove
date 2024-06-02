package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
    @Column(name ="id")
    private Long id;
    @Column(nullable = false,unique = true, name = "username")
    private  String username;
    @Column(nullable = false, name = "password")
    private  String password;
    @Column(nullable = false, unique = true, name="email")
    private String email;

    @Column(name= "verified", nullable = false)
    private  Boolean verified;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id", referencedColumnName = "id")
    private File image;
    @Column(name = "locale", nullable = false)
    private String locale;

    @Column(name = "owner")
    private  Boolean Owner;

        @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "modders", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "community_id"))
    private List<Community> modderCommunities;


    public User() {
        //FOR HIBERNATE JPA
    }

    public User(final String username, final String password, final String email, Boolean verified) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.locale = "es";
    }

    public User(final String username, final String password, final String email, Boolean verified, String locale,Boolean owner) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.locale = locale;
        this.Owner=owner;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Boolean isVerified() {
        return verified;
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

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File file) {
        this.image = file;
    }

    public long getPortraid_id() {
        return image.getImageId();
    }
    public List<Community> getModderCommunities() {
        return modderCommunities;
    }

    public void setModderCommunities(List<Community> modderCommunities) {
        this.modderCommunities = modderCommunities;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getOwner() {
        return Owner;
    }

    public void setOwner(Boolean owner) {
        Owner = owner;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
