package ar.edu.itba.paw.services;

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

    @Override
    public List<Post> getAllPosts() {
        List<Post> posts = postDao.findAllPosts();
        if(posts.isEmpty())
            return Collections.emptyList();
        return posts;
    }

    @Override
    public void createPost(String title, String body, String username) {
        Optional<User> user = userService.findByUsername(username);
        if(!user.isPresent())
            throw new IllegalArgumentException("User not found");
        long userId = user.get().getId();
        postDao.createPost(title,body,(int)userId,1,false, LocalDateTime.now());
    }


}
