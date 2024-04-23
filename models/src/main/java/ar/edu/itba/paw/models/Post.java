package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Post {

    private final long id;
    private final String title;
    private final String body;
    private final long authorId;
    private final String communityName;
    private final boolean media;
    private final long mediaId;
    private final LocalDateTime date;
    private final int grooviness;
    private final boolean deleted;

    private final String category;


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getAuthorId() {
        return authorId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public boolean getMedia() {
        return media;
    }

    public long getMediaId() {
        return mediaId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getGrooviness() {
        return grooviness;
    }
    public String getCategory() {
        return category;
    }
    public Post(final long id, final String title, final String body, final long author_id, final String community_name, final boolean media, final long media_id, final LocalDateTime date, final int grooviness, boolean deleted, final String category) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.authorId = author_id;
        this.communityName = community_name;
        this.media = media;
        this.mediaId = media_id;
        this.date = date;
        this.grooviness = grooviness;
        this.deleted = deleted;
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
