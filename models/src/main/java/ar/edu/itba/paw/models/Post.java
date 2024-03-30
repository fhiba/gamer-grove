package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Post {

    private final long id;
    private final String title;
    private final String body;
    private final long author_id;
    private final long sub_id;
    private final boolean media;
    private final long media_id;
    private final LocalDateTime date;
    private final int grooviness;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public long getSub_id() {
        return sub_id;
    }

    public boolean getMedia() {
        return media;
    }

    public long getMedia_id() {
        return media_id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getGrooviness() {
        return grooviness;
    }

    public Post(final long id, final String title, final String body, final long author_id, final long sub_id, final boolean media, final long media_id, final LocalDateTime date, final int grooviness) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author_id = author_id;
        this.sub_id = sub_id;
        this.media = media;
        this.media_id = media_id;
        this.date = date;
        this.grooviness = grooviness;
    }

}
