package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(final long id);

    List<Post> findAllPosts();

    Post createPost(String title, String body, User author, Community community, boolean media, LocalDateTime now,
            String category);

    List<Post> findByCategory(String category);

    void editGrooviness(long postId, GroovyEnum groovy);

    void addToGroovy(User user, Post post, GroovyEnum groovy);

    Optional<GroovyEnum> checkGrooviness(long postId, long userId);

    void deleteGrooviness(long postId, long id);

    void updateGroovyHistory(long postId, long id, GroovyEnum groovyEnum);

    List<Post> getMyFollowedPosts(long userId);

    List<Post> getMyFollowedPostsByCategory(String category, long userId);

    List<Post> findPostsByUser(long id);

    List<String> getUsedCategories();

    int getTotalPostCount();

    int getTotalPostByCommunityCount(String communityName);

    List<Post> getPostsByCommunityPaginated(String communityName, int pageSize, int offset);

    int getTotalUserLikedPostCount(long userId);

    List<Post> getUserLikedPostPaginated(long userId, int pageSize, int offset);

    List<Post> getNewsLimited(int limit);

    List<Post> getPostsByUserPaginated(long id, int pageSize, int offset);

    int getTotalPostsByUser(long id);

    void removePost(Post post);

    List<Post> topFivePosts();

    List<Post> find(int pageSize, int offset, PostCategories category, PostOrders order, Long userId,
            Long authorId, String community, Long likerId);

    Integer findCount(PostCategories category, Long userId,
            Long authorId, String community, Long likerId);
}
