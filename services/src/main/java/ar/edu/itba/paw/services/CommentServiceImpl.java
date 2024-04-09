package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserService userService;

    @Override
    public Comment createComment(long postId, String body) {
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new IllegalArgumentException("User not found");
        long userId = user.get().getId();
        String username = user.get().getUsername();
        return commentDao.createComment(postId,body,username,LocalDateTime.now(),userId);
    }

    @Override
    public List<Comment> getPostComments(long postId) {
        return commentDao.getPostComments(postId);
    }
}
