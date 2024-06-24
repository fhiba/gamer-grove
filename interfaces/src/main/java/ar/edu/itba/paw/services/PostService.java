package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import org.springframework.web.multipart.MultipartFile;
import ar.edu.itba.paw.models.User;

import java.util.List;


public interface PostService {

    List<Post> getAllPosts();

    Post createPost(final String title, final String content, final String communityName, final String category, final MultipartFile[] files) throws NoLoggedUserException, NoSuchCommunityException;


    PaginatedDataWrapper<Post> getPostsByCommunityPaginated(String communityName, PaginationRequest request);

    List<Post> getByCategory(final String category);

    Post getPostById(long postId) throws NoSuchPostException;

    Post getPostByIdWithImage(long postId) throws NoSuchPostException;

    void editGrooviness(int grooviness, long postId) throws UserNotFoundException, NoSuchPostException, NoLoggedUserException;

    int checkGrooviness(long postId);

    List<Post> getMyFollowedPosts(User user) throws NoLoggedUserException;

    PaginatedDataWrapper<Post> getUserFollowedPostsPaginated(String category, String order, long userId, PaginationRequest request);

    List<Post> getMyFollowedPostsByCategory(String category, User user) throws NoLoggedUserException;

    List<Post> getPostsByUser(long id);

    List<Post> getUserLikedPosts(long id);

    List<Post> getNewsLimited(int limit);

    PaginatedDataWrapper<Post> getUserLikedPostsPaginated(long userId, PaginationRequest request);

    List<String> getUsedCategories();
    void removePost(final long postId) throws NoSuchPostException;

    List<Post> topFivePosts();


    PaginatedDataWrapper<Post> getAllPostsPaginated(String category, String order, PaginationRequest paginationRequest);

    PaginatedDataWrapper<Post> getPostsByUserPaginated(long id, PaginationRequest paginationRequest);
}
