package ar.edu.itba.paw.services;

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
    public void createPost(final String title, final String body, final String communityName) {
        Optional<User> user = userService.getLoggedUser();
        if(!user.isPresent())
            throw new IllegalArgumentException("User not found");
        long userId = user.get().getId();
        Optional<Community> community = communityService.findByName(communityName);
        if(!community.isPresent())
            throw new IllegalArgumentException("Community not found");
        postDao.createPost(title,body,(int)userId,communityName,false, LocalDateTime.now());
    }

    @Override
    public List<Post> getPostsByCommunity(final String communityName) {
        List<Post> posts = postDao.findPostsByCommunity(communityName);
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }


}
