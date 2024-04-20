package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(final long id);

    List<Post>  findAllPosts();

    Post createPost(String title, String body, int author_id, String community_name, boolean media, LocalDateTime now, String category);

    List<Post> findPostsByCommunity(String communityId);
    List<Post> findByCategory(String category);

    void editGrooviness(long postId, int i);

    void addToGroovy(long userId, long postId, boolean grooviness);

    Optional<Boolean> checkGrooviness(long postId, long userId);

    void insertIntoGroovyHistory(long postId, long id, boolean grooviness);

    void deleteGrooviness(long postId, long id);

    void updateGroovyHistory(long postId, long id, boolean b);

    List<Post> findPostsByUser(long id);

    List<Post> findPostsLikedByUser(long id);
}
