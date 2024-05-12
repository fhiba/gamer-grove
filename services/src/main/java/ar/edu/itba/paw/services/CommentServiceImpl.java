package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.pagination.PaginationRequest;
import ar.edu.itba.paw.persistance.CommentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ar.edu.itba.paw.exceptions.NoSuchCommentException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);


    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private PostService postService;

    @Autowired
    private ModderService modderService;

    @Transactional
    @Override
    public Comment createComment(long postId, String body) throws NoLoggedUserException{
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new NoLoggedUserException("User not logged");
        long userId = user.get().getId();
        String username = user.get().getUsername();
        LocalDateTime date = LocalDateTime.now();
        Comment comment = commentDao.createComment(postId,body,username,date,userId);
        sendMailToPostOwner(postId,date);
        return comment;
    }
    @Async
    void sendMailToPostOwner(long postId, LocalDateTime date) {
        Post post;

        try{
            post = postService.getPostById(postId);
        } catch (NoSuchPostException e){
            return;
        }

        Optional<User> user = userService.findById(post.getAuthorId());
        if(user.isEmpty())
            return;
        mailingService.sendNewCommentNotification(user.get(), post, date);
    }


    @Override
    public List<Comment> getPostComments(long postId) {
        return commentDao.getPostComments(postId);
    }

    @Override
    public PaginatedDataWrapper<Comment> getPostCommentsPaginated(long postId, PaginationRequest request) {
        if( request.getPageSize() < 1){
            throw new IllegalArgumentException("Invalid Page size");
        }
        if(postId < 1){
            throw new IllegalArgumentException("Invalid Post id");
        }
        int totalCount = commentDao.getPostCommentsTotalCount(postId);
        if(request.getPageNumber() <1 ){
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Comment> data = commentDao.getPostCommentsPaginated(postId,request.getPageSize(), offset);
        PaginatedDataWrapper<Comment> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(), totalCount, request.getPageSize());
        if(request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0){
            throw new IllegalArgumentException("Invalid Page number");
        }

        return dataWrapper;
    }

    @Transactional
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

    @Transactional
    @Override
    public int deleteComment(long commentId) throws NoSuchCommentException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        if(comment.isEmpty())
            throw new NoSuchCommentException("Comment not found");
        int ret = commentDao.deleteComment(commentId);
        notifyCommentDeletion(comment.get());
        return  ret;
    }

    @Async
    public void notifyCommentDeletion(Comment comment) {
        Optional<User> user = userService.findByUsername(comment.getUsername());
        if(user.isEmpty()) {
            return;
        }
        Post post;
        try {
            post = postService.getPostById(comment.getPostId());
        } catch (NoSuchPostException e) {
            return;
        }
        mailingService.notifyCommentDeletion(user.get().getEmail(), user.get().getUsername(), comment.getPostId(), post.getTitle(), post.getCommunityName(), comment.getBody());
    }
}
