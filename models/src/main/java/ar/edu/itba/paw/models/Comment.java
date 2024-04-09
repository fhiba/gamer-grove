package ar.edu.itba.paw.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private final long id;

    private final long postId;

    private final String username;

    private final long parentId;
    private final String body;
    private final LocalDateTime date;
    private final int grooviness;



    public Comment(long id, long postId, String username, long parentId, String body, LocalDateTime date, int grooviness) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.parentId = parentId;
        this.body = body;
        this.date = date;
        this.grooviness = grooviness;
    }

    public Comment(long id, long postId, String username, String body, LocalDateTime date, int grooviness) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.grooviness = grooviness;
        this.parentId = -1;
        this.body = body;
        this.date = date;
    }


    public long getId() {
        return id;
    }

    public long getPostId() {
        return postId;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public int getGrooviness() {
        return grooviness;
    }
}
