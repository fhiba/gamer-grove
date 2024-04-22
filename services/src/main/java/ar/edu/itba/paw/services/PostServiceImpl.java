package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserService userService;
    @Autowired
    private CommunityService communityService;

    @Autowired
    private MailingService mailingService;

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postDao.findAllPosts();
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Override
    public Post createPost(final String title, final String body, final String communityName, final String category) throws NoLoggedUserException, NoSuchCommunityException {
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new NoLoggedUserException("User not logged");
        long userId = user.get().getId();
        Community community = communityService.findByName(communityName);
        Post post = postDao.createPost(title,body,(int)userId,community.getName(),false, LocalDateTime.now(), category);
        notifyUsers(post, user.get());

        return post;
    }
    @Async
    void notifyUsers(Post post, User user) {
        // TODO: BRING USERS FROM COMMUNITY
        System.out.println("notifying");
        //List<User> users = userService.getUsersByCommunity(post.getCommunity_name());
        List<User> users = userService.findAll();
        mailingService.sendNewPostNotifications(users, post, user);
    }

    @Override
    public List<Post> getPostsByCommunity(final String communityName) {
        List<Post> posts = postDao.findPostsByCommunity(communityName);
        return posts.isEmpty()? Collections.emptyList(): posts;

    }

    @Override
    public List<Post> getByCategory(String category) {
        List<Post> posts = postDao.findByCategory(category);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public Post getPostById(long postId) throws NoSuchPostException{
        Optional<Post> post = postDao.findById(postId);
        if(post.isEmpty())
            throw new NoSuchPostException("Post with id:" + postId+ " not found");
        return post.get();
    }

    @Override
    public void editGrooviness(int grooviness, long postId) throws UserNotFoundException, NoSuchPostException {
        Optional<Post> post = postDao.findById(postId);
        //TODO : USE NEW EXCEPTION
        User user = userService.getLoggedUser().orElseThrow(() -> new UserNotFoundException("User not found"));
        if(post.isEmpty()) {
            throw new NoSuchPostException("Post not found");
        }
        //checks whether the user has already grooved the comment
        Optional<Boolean> isGroovy = postDao.checkGrooviness(postId, user.getId());
        if(isGroovy.isEmpty()) {
            postDao.insertIntoGroovyHistory(postId, user.getId(),(grooviness == 1));
            postDao.editGrooviness(postId,grooviness);
            return;
        }


        switch (grooviness){
            case 1:
                if(isGroovy.get()) {
                    postDao.deleteGrooviness(postId, user.getId());
                    postDao.editGrooviness(postId, -1);
                } else {
                    postDao.editGrooviness(postId,2);
                    postDao.updateGroovyHistory(postId, user.getId(), true);
                }
                break;
            case -1:
                if(isGroovy.get()) {
                    postDao.editGrooviness(postId,-2);
                    postDao.updateGroovyHistory(postId, user.getId(), false);
                }
                else {
                    postDao.deleteGrooviness(postId, user.getId());
                    postDao.editGrooviness(postId,1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
    }

    @Override
    public int checkGrooviness(long postId) {
        Optional<Boolean> maybeGroovy = postDao.checkGrooviness(postId, userService.getLoggedUser().get().getId());
        return maybeGroovy.map(aBoolean -> aBoolean ? 1 : -1).orElse(0);
    }

    @Override
    public List<Post> getMyFollowedPosts(User user) {
        List<Post> posts = postDao.getMyFollowedPosts(user.getId());
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Override
    public List<Post> getMyFollowedPostsByCategory(String category, User user) {
        List<Post> posts = postDao.getMyFollowedPostsByCategory(category,user.getId());
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Override
    public List<Post> getPostsByUser(long id) {
        List<Post> posts = postDao.findPostsByUser(id);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }

    @Override
    public List<Post> getUserLikedPosts(long id) {
        List<Post> posts = postDao.findPostsLikedByUser(id);
        return posts.isEmpty()? Collections.emptyList(): posts;
    }


}
