package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Post;

import java.util.List;


public interface PostService {

    List<Post> getAllPosts();

    void createPost(final String title, final String content, final String communityName, final String category);

    List<Post> getPostsByCommunity(final String communityName);

    List<Post> getByCategory(final String category);
    Post getPostById(long postId) throws NoSuchPostException;

    void editGrooviness(long postId, int grooviness) throws NoSuchPostException, UserNotFoundException;
}
