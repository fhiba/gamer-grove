package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postDao.findAllPosts();
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Override
    public void createPost(final String title, final String body, final String communityName, final String category) {
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new IllegalArgumentException("User not found");
        long userId = user.get().getId();
        Community community = communityService.findByName(communityName);
        postDao.createPost(title,body,(int)userId,community.getName(),false, LocalDateTime.now(), category);
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
            throw new NoSuchPostException("Post not found");
        return post.get();
    }

    @Override
    public void editGrooviness(long postId, int grooviness) throws NoSuchPostException, UserNotFoundException {
        Optional<Post> post = postDao.findById(postId);
        User user = userService.getLoggedUser().orElseThrow(() -> new UserNotFoundException("User not found"));
        if(post.isEmpty())
            throw new NoSuchPostException("Post not found");

        switch (grooviness){
            case 1:
                postDao.editGrooviness(postId,1);
                postDao.addToGroovy(user.getId(),postId,true);
                break;
            case -1:
                postDao.editGrooviness(postId,-1);
                postDao.addToGroovy(user.getId(),postId,false);
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness");
        }
    }


}
