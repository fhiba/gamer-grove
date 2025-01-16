package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.PostDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserService userService;
    @Autowired
    private CommunityService communityService;

    @Autowired
    private MailingService mailingService;
    @Autowired
    private FileService fs;

    @Transactional
    @Override
    public Post createPost(final String title, final String body, final String communityName, final String category,
            final List<byte[]> files) throws NoLoggedUserException, NoSuchCommunityException {
        Optional<User> maybeUser = userService.getLoggedUser();
        if (maybeUser.isEmpty()) {
            LOGGER.atError().setMessage("No logged user found when creating a post").log();
            throw new NoLoggedUserException();
        }
        User user = maybeUser.get();
        Community community = communityService.findByName(communityName);
        Post post = postDao.createPost(title, body, user, community, false, LocalDateTime.now(), category);
        notifyUsers(post, user);
        if (files != null) {
            for (byte[] file : files) {
                if (!Objects.isNull(file) && file.length > 0) {
                    fs.uploadPostImage(file, post.getId());
                }
            }
        }
        LOGGER.atInfo().setMessage("New post {} created successfully").addArgument(() -> post.getId()).log();
        return post;
    }

    @Async
    void notifyUsers(Post post, User user) {
        Community community = post.getcommunity();

        List<User> users = userService.getFollowersOfCommunity(community.getId());

        mailingService.sendNewPostNotifications(users, post, user);
    }

    @Override
    public PaginatedDataWrapper<Post> getPostsByCommunityPaginated(final String communityName,
            PaginationRequest request) {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostByCommunityCount(URLDecoder.decode(communityName, StandardCharsets.UTF_8));
        if (totalCount == 0) {
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount,
                    request.getPageSize());
        }

        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getPostsByCommunityPaginated(URLDecoder.decode(communityName, StandardCharsets.UTF_8),
                request.getPageSize(), offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public List<Post> getByCategory(String category) {
        List<Post> posts = postDao.findByCategory(category);
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    @Override
    public Post getPostById(long postId) throws NoSuchPostException {
        Optional<Post> post = postDao.findById(postId);
        if (post.isEmpty()) {
            LOGGER.atWarn().setMessage("No post with id {} found").addArgument(postId).log();
            throw new NoSuchPostException();
        }
        return post.get();
    }

    @Override
    public Post getPostByIdWithImage(long postId) throws NoSuchPostException {
        Optional<Post> post = postDao.findById(postId);
        if (post.isEmpty()) {
            LOGGER.atWarn().setMessage("No post with id {} found").addArgument(postId).log();
            throw new NoSuchPostException();
        }
        return post.get();
    }

    @Transactional
    @Override
    public void createGrooviness(GroovyEnum groovyness, long postId) throws NoSuchPostException, NoLoggedUserException {
        Optional<Post> post = postDao.findById(postId);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        if (post.isEmpty()) {
            LOGGER.atWarn().setMessage("No post with id {} found").addArgument(postId).log();
            throw new NoSuchPostException();
        }

        postDao.addToGroovy(user, post.get(), groovyness);
        postDao.editGrooviness(postId, groovyness);

    }

    @Transactional
    @Override
    public void deleteGrooviness(long postId) throws NoSuchPostException, NoLoggedUserException {
        Optional<Post> post = postDao.findById(postId);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        if (post.isEmpty()) {
            LOGGER.atWarn().setMessage("No post with id {} found").addArgument(postId).log();
        }
        Optional<GroovyEnum> groovyness = postDao.checkGrooviness(postId, user.getId());
        if (groovyness.isEmpty()) {
            return;
        }
        postDao.deleteGrooviness(postId, user.getId());
        GroovyEnum modifier = groovyness.get() == GroovyEnum.UP ? GroovyEnum.DOWN : GroovyEnum.UP;
        postDao.editGrooviness(postId, modifier);
    }

    @Transactional
    @Override
    public void editGrooviness(GroovyEnum grooviness, long postId) throws NoSuchPostException, NoLoggedUserException {
        Optional<Post> post = postDao.findById(postId);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        if (post.isEmpty()) {
            LOGGER.atWarn().setMessage("No post with id {} found").addArgument(postId).log();
            // TODO: Ver que este ya fixeado en la interface para no pifiar los msjs
            throw new NoSuchPostException();
        }

        Optional<GroovyEnum> isGroovy = postDao.checkGrooviness(postId, user.getId());
        // TODO: tecnicamente si no existe no deberia dejarte seguir... Controlado en el
        // TODO: auth y 500 por hacer un get si es null
        GroovyEnum toUpdate = isGroovy.get();
        switch (grooviness) {
            case GroovyEnum.UP:
                if (toUpdate == GroovyEnum.UP) {
                    return;
                } else {
                    postDao.editGrooviness(postId, GroovyEnum.UP_FROM_DOWN);
                    postDao.updateGroovyHistory(postId, user.getId(), GroovyEnum.UP);
                }
                break;
            case GroovyEnum.DOWN:
                if (toUpdate == GroovyEnum.UP) {
                    postDao.editGrooviness(postId, GroovyEnum.DOWN_FROM_UP);
                    postDao.updateGroovyHistory(postId, user.getId(), GroovyEnum.DOWN);
                } else {
                    return;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
        LOGGER.atInfo().setMessage("User {} upvote post with id {} sucessfully")
                .addArgument(() -> post.get().getAuthor().getUsername()).addArgument(postId).log();
    }

    @Override
    public Optional<GroovyEnum> checkGrooviness(long postId) throws NoLoggedUserException, NoSuchPostException {
        long userId = userService.getLoggedUser().map(User::getId).orElseThrow(NoLoggedUserException::new);
        Optional<Post> post = postDao.findById(postId);

        return postDao.checkGrooviness(post.map(Post::getId).orElseThrow(NoSuchPostException::new), userId);
    }

    @Override
    public PaginatedDataWrapper<Post> getUserFollowedPostsPaginated(String category, String order, long userId,
            PaginationRequest request) {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.findCount(PostCategories.fromString(category), userId, null, null, null);

        if (totalCount == 0) {
            return new PaginatedDataWrapper<>(Collections.emptyList(), request.getPageNumber(), totalCount,
                    request.getPageSize());
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.find(request.getPageSize(), offset, PostCategories.fromString(category),
                PostOrders.fromString(order), userId, null, null, null);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public List<Post> getPostsByUser(long id) {
        List<Post> posts = postDao.findPostsByUser(id);
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    @Override
    public List<Post> getNewsLimited(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        List<Post> posts = postDao.getNewsLimited(limit);
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    @Override
    public PaginatedDataWrapper<Post> getUserLikedPostsPaginated(long userId, PaginationRequest request) {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalUserLikedPostCount(userId);
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getUserLikedPostPaginated(userId, request.getPageSize(), offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Override
    public List<String> getUsedCategories() {
        List<String> categories = postDao.getUsedCategories();
        return categories.isEmpty() ? Collections.emptyList() : categories;
    }

    @Override
    public PaginatedDataWrapper<Post> getAllPostsPaginated(String category, String order, PaginationRequest request,
            Long likerId, Long authorId, String community, Boolean fromFollowedCommunities)
            throws IllegalPageException, PageNotFoundException {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException();
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalPageException();
        }
        Optional<User> maybeLogged = userService.getLoggedUser();
        if (maybeLogged.isEmpty() && fromFollowedCommunities) {
            throw new NoLoggedUserException();
        }
        Long userId = fromFollowedCommunities ? maybeLogged.map(User::getId).orElse(null) : null;
        int totalCount = postDao.findCount(PostCategories.fromString(category), userId, authorId, community, likerId);

        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.find(request.getPageSize(), offset, PostCategories.fromString(category),
                PostOrders.fromString(order), userId, authorId, community, likerId);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new PageNotFoundException();
        }
        return dataWrapper;
    }

    @Override
    public PaginatedDataWrapper<Post> getPostsByUserPaginated(long id, PaginationRequest request) {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int totalCount = postDao.getTotalPostsByUser(id);
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Post> data = postDao.getPostsByUserPaginated(id, request.getPageSize(), offset);
        PaginatedDataWrapper<Post> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount,
                request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        return dataWrapper;
    }

    @Transactional
    @Override
    public void removePost(long postId) throws NoSuchPostException {
        Post post = getPostById(postId);
        postDao.removePost(post);
        notifyDeletion(postId);
        LOGGER.atInfo().setMessage("Post with id {} removed sucessfully").addArgument(postId).log();
    }

    @Override
    public List<Post> topFivePosts() {
        return postDao.topFivePosts();
    }

    @Async
    public void notifyDeletion(Long postId) {
        Post post;
        try {
            post = getPostById(postId);
        } catch (NoSuchPostException e) {
            LOGGER.atError().setMessage("Post with id {} not found").addArgument(postId).log();
            return;
        }
        long authorId = post.getAuthor().getId();
        Optional<User> author = userService.findById(authorId);

        if (author.isEmpty()) {
            LOGGER.atError().setMessage("Author of post {} with id {} not found").addArgument(postId)
                    .addArgument(authorId).log();
            return;
        }
        User authorUser = author.get();
        mailingService.notifyPostDeletion(authorUser.getEmail(), authorUser.getUsername(), post.getId(),
                post.getTitle(), post.getcommunity().getName(), Locale.of(authorUser.getLocale()));
    }

}
