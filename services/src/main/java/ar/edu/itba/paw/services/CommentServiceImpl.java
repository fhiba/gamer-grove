package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
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

    @Autowired
    private GroovyCommentHistoryService groovyCommentHistoryService;

    @Transactional
    @Override
    public Comment createComment(long postId, String body) throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException {
        Optional<User> user = userService.getLoggedUser();
        if(user.isEmpty())
            throw new NoLoggedUserException("User not logged");
        Post post = postService.getPostById(postId);
        if(post.isDeleted()){
            throw new PostIsDeletedException("Post is deleted");
        }
        long userId = user.get().getId();
        User username = user.get();
        LocalDateTime date = LocalDateTime.now();
        Comment comment = commentDao.createComment(post,body,username,date,userId);
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

        Optional<User> user = userService.findById(post.getAuthor().getId());
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
    public void editGroovinessOnComment(long commentId, int grooviness, long postId) throws NoSuchCommentException, NoLoggedUserException, NoSuchPostException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        User user = userService.getLoggedUser().orElseThrow(() -> new NoLoggedUserException("User not found"));

        if(comment.isEmpty())
            throw new NoSuchCommentException("Comment not found");
        //checks whether the user has already grooved the comment
        Post post = postService.getPostById(postId);

        LOGGER.info("params: " + commentId + " " + grooviness + " " + postId);
        Optional<GroovyCommentHistory> maybeGCH = groovyCommentHistoryService.findGroovyCommentHistory(user, comment.get(), post);
        LOGGER.info("maybeGCH: " + maybeGCH);
        if(maybeGCH.isEmpty()) {
            groovyCommentHistoryService.createGroovyCommentHistory(user, comment.get(), post, grooviness == 1);
            commentDao.editGrooviness(comment.get(),grooviness);
            return;
        }


        Boolean isGroovy = maybeGCH.get().isGroovy();
        switch (grooviness){
            case 1:
                if(isGroovy) {
                    groovyCommentHistoryService.deleteGroovyCommentHistory(user, comment.get());
                    commentDao.editGrooviness(comment.get(), -1);
                } else {
                    commentDao.editGrooviness(comment.get(),2);
                    groovyCommentHistoryService.updateGroovyCommentHistory(maybeGCH.get(), true);
                }
                break;
            case -1:
                if(isGroovy) {
                    commentDao.editGrooviness(comment.get(),-2);
                    groovyCommentHistoryService.updateGroovyCommentHistory(maybeGCH.get(), false);
                }
                else {
                    groovyCommentHistoryService.deleteGroovyCommentHistory(user, comment.get());
                    commentDao.editGrooviness(comment.get(),1);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
    }


    @Override
    public List<Comment> getUpGroovedComments(long postId) throws UserNotFoundException {
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
    public int deleteComment(long commentId) throws NoSuchCommentException, NoSuchPostException, PostIsDeletedException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        if(comment.isEmpty())
            throw new NoSuchCommentException("Comment not found");
        Post post = postService.getPostById(comment.get().getPostId());
        if(post.isDeleted())
            throw new PostIsDeletedException("Post not found");

        int ret = commentDao.deleteComment(comment.get());
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
        mailingService.notifyCommentDeletion(user.get().getEmail(), user.get().getUsername(), comment.getPostId(), post.getTitle(), post.getcommunity().getName(), comment.getBody());
    }
}
