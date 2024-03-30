package ar.edu.itba.paw.models;

public class User {
    private final String username;
    private final String password;
    private final String email;
    private final long id;
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

    public User(final long id, final String username, final String password, final String email) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.password = password;
    }
}
