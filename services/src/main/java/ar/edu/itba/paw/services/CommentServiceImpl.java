package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.itba.paw.exceptions.NoSuchCommentException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Override
    public Comment createComment(long postId, String body) throws NoLoggedUserException{
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new NoLoggedUserException("User not logged");
        long userId = user.get().getId();
        String username = user.get().getUsername();
        return commentDao.createComment(postId,body,username,LocalDateTime.now(),userId);
    }

    @Override
    public List<Comment> getPostComments(long postId) {
        return commentDao.getPostComments(postId);
    }

    @Override
    public void editGroovinessOnComment(long commentId, int grooviness, long postId) throws NoSuchCommentException, UserNotFoundException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        //TODO : USE NEW EXCEPTION
        User user = userService.getLoggedUser().orElseThrow(() -> new UserNotFoundException("User not found"));

        if(comment.isEmpty())
            throw new NoSuchCommentException("Comment not found");
        //checks whether the user has already grooved the comment
        Optional<Boolean> isGroovy = commentDao.getGroovyTypeFromComment(commentId, user.getId(), postId);
        if(isGroovy.isEmpty()) {
            commentDao.insertGroovinessIntoComment(commentId, user.getId(), postId, (grooviness == 1));
            commentDao.editGrooviness(commentId,grooviness);
            return;
        }


        switch (grooviness){
            case 1:
                if(isGroovy.get()) {
                    commentDao.deleteGrooviness(commentId, user.getId(), postId);
                    commentDao.editGrooviness(commentId, -1);
                } else {
                    commentDao.editGrooviness(commentId,2);
                    commentDao.updateGroovyHistory(commentId, user.getId(), postId, true);
                }
                break;
            case -1:
                if(isGroovy.get()) {
                    commentDao.editGrooviness(commentId,-2);
                    commentDao.updateGroovyHistory(commentId, user.getId(), postId, false);
                }
                else {
                    commentDao.deleteGrooviness(commentId, user.getId(), postId);
                    commentDao.editGrooviness(commentId,1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
    }

    @Override
    public List<Comment> getUpGroovedComments(long postId) throws NoSuchPostException, UserNotFoundException {
        Optional<User> possibleUser = userService.getLoggedUser();
        if(possibleUser.isEmpty())
            throw new UserNotFoundException("User not found");
        User user = possibleUser.get();
        //chequeo que el post exista
        //Post post = postService.getPostById(postId); chequear si hace falta validar aca, creemos que no
        return commentDao.getGroovedComments(postId,user.getId());
    }

    @Override
    public List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException {
        Optional<User> possibleUser = userService.getLoggedUser();
        if(possibleUser.isEmpty())
            throw new UserNotFoundException("User not found");
        User user = possibleUser.get();
        return commentDao.getDownGroovedComments(postId,user.getId());
    }
}
