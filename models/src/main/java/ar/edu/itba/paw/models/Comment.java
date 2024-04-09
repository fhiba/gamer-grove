package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Comment {
    private final long id;

    private final long postId;

    private final String username;

    private final long parentId;
    private final String body;
    private final LocalDateTime date;



    public Comment(long id, long postId, String username, long parentId, String body, LocalDateTime date) {
        this.id = id;
        this.postId = postId;
        this.username = username;
        this.parentId = parentId;
        this.body = body;
        this.date = date;
    }

    public Comment(long id, long postId, String username, String body, LocalDateTime date) {
        this.id = id;
        this.postId = postId;
        this.username = username;
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
}
