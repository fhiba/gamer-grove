package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.IllegalPageException;
import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommunityException;
import ar.edu.itba.paw.exceptions.NoSuchGroovyPostHistory;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.PageNotFoundException;
import ar.edu.itba.paw.exceptions.PostAlreadyGroovedException;
import ar.edu.itba.paw.exceptions.PostIsDeletedException;
import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post createPost(final String title, final String content, final String communityName, final String category,
            final List<byte[]> files) throws NoLoggedUserException, NoSuchCommunityException;

    PaginatedDataWrapper<Post> getPostsByCommunityPaginated(String communityName, PaginationRequest request);

    List<Post> getByCategory(final String category);

    Post getPostById(long postId) throws NoSuchPostException;

    Post getPostByIdWithImage(long postId) throws NoSuchPostException;

    void editGrooviness(GroovyEnum grooviness, long postId)
            throws NoSuchPostException, NoLoggedUserException, NoSuchGroovyPostHistory, PostIsDeletedException;

    void deleteGrooviness(long postId)
            throws NoSuchPostException, NoLoggedUserException, NoSuchGroovyPostHistory, PostIsDeletedException;

    void createGrooviness(GroovyEnum groovyness, long postId)
            throws NoSuchPostException, NoLoggedUserException, PostAlreadyGroovedException, PostIsDeletedException;

    Optional<GroovyEnum> checkGrooviness(long postId)
            throws NoLoggedUserException, NoSuchPostException, NoSuchGroovyPostHistory;

    PaginatedDataWrapper<Post> getUserFollowedPostsPaginated(String category, String order, long userId,
            PaginationRequest request);

    List<Post> getPostsByUser(long id);

    List<Post> getNewsLimited(int limit);

    PaginatedDataWrapper<Post> getUserLikedPostsPaginated(long userId, PaginationRequest request);

    List<String> getUsedCategories();

    void removePost(final long postId) throws NoSuchPostException;

    List<Post> topFivePosts();

    PaginatedDataWrapper<Post> getAllPostsPaginated(String category, String order, PaginationRequest paginationRequest,
            Long likerId, Long authorId, String community, Boolean fromFollowedCommunities)
            throws PageNotFoundException, IllegalPageException, NoLoggedUserException;

    PaginatedDataWrapper<Post> getPostsByUserPaginated(long id, PaginationRequest paginationRequest);

    List<Post> getPostsByUserName(String userName);
}
