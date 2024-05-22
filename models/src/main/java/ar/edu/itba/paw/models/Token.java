package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "token")
public class Token {

    @Id
    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;
    @Column(name = "type")
    private String type;

    public Token() {

    }

    public Token(String value, User user, String type) {
        this.value = value;
        this.user = user;
        this.type = type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
