package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Post;
import org.springframework.web.multipart.MultipartFile;
import ar.edu.itba.paw.models.User;

import java.util.List;


public interface PostService {

    List<Post> getAllPosts();

    Post createPost(final String title, final String content, final String communityName, final String category, final MultipartFile[] files) throws NoLoggedUserException, NoSuchCommunityException;

    List<Post> getPostsByCommunity(final String communityName);

    List<Post> getByCategory(final String category);
    Post getPostById(long postId) throws NoSuchPostException;

    public Post getPostByIdWithImage(long postId) throws NoSuchPostException;

    void editGrooviness(int grooviness, long postId) throws UserNotFoundException, NoSuchPostException;

    int checkGrooviness(long postId);

    List<Post> getMyFollowedPosts(User user) throws NoLoggedUserException;

    List<Post> getMyFollowedPostsByCategory(String category, User user) throws NoLoggedUserException;

    List<Post> getPostsByUser(long id);

    List<Post> getUserLikedPosts(long id);
}
