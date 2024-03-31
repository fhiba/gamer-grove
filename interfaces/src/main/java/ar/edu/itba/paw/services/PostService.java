package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Post;

import java.util.List;


public interface PostService {

    List<Post> getAllPosts();

    void createPost(final String title, final String content, final String communityName);

    List<Post> getPostsByCommunity(final String communityName);
}
