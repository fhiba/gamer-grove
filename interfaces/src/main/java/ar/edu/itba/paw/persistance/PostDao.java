package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    Optional<Post> findById(final long id);

    List<Post> findAllPosts();

    Post createPost(String title, String body, User author, Community community, boolean media, LocalDateTime now, String category);

    List<Post> findPostsByCommunity(String communityId);

    List<Post> findByCategory(String category);

    void editGrooviness(long postId, int i);

    void addToGroovy(User user, Post post, boolean grooviness);

    Optional<Boolean> checkGrooviness(long postId, long userId);

    void insertIntoGroovyHistory(long postId, long id, boolean grooviness);

    void deleteGrooviness(long postId, long id);

    void updateGroovyHistory(long postId, long id, boolean b);

    Optional<Post> findByIdWithImage(final long id);

    List<Post> getMyFollowedPosts(long userId);

    List<Post> getMyFollowedPostsByCategory(String category, long userId);

    List<Post> findPostsByUser(long id);

    List<Post> findPostsLikedByUser(long id);

    List<String> getUsedCategories();

    int getTotalPostCount();

    List<Post> getAllPostsPaginated(int pageSize, int offset);

    int getTotalPostByCategoryCount(String category);

    List<Post> getAllPostsByCategoryPaginated(String category, int pageSize, int offset);

    int getTotalPostByCommunityCount(String communityName);

    List<Post> getPostsByCommunityPaginated(String communityName, int pageSize, int offset);

    int getTotaltFollowedPostsByUserCount(long userId);

    List<Post> getFollowedPostsByUserPaginated(long userId, int pageSize, int offset);

    int getTotalUserFollowedPostsByCategoryCount(long userId, String category);

    List<Post> getUserFollowedPostsByCategoryPaginated(long userId, String category, int pageSize, int offset);

    int getTotalUserLikedPostCount(long userId);

    List<Post> getUserLikedPostPaginated(long userId, int pageSize, int offset);

    List<Post> getNewsLimited(int limit);

    List<Post> getPostsByUserPaginated(long id, int pageSize, int offset);

    int getTotalPostsByUser(long id);

    void removePost(Post post);

    List<Post> topFivePosts();
}