package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(final long id);

    List<Post>  findAllPosts();

    void createPost(String title, String body, int author_id, int community_id, boolean media, LocalDateTime now);
}
