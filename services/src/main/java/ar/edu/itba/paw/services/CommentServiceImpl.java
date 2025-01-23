package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.exceptions.CommentAlreadyGroovedException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
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
import java.util.Locale;
import java.util.Objects;
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
    private GroovyCommentHistoryService groovyCommentHistoryService;

    @Transactional
    @Override
    public Comment createComment(long postId, String body)
            throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException {
        Optional<User> user = userService.getLoggedUser();
        if (user.isEmpty()) {
            LOGGER.atError().setMessage("No user logged").log();
            throw new NoLoggedUserException();
        }
        Post post = postService.getPostById(postId);
        if (post.getDeleted()) {
            LOGGER.atError().setMessage("Post with id {} is deleted").addArgument(postId).log();
            throw new PostIsDeletedException();
        }
        long userId = user.get().getId();
        User username = user.get();
        LocalDateTime date = LocalDateTime.now();
        Comment comment = commentDao.createComment(post, body, username, date, userId);
        sendMailToPostOwner(postId, date);
        LOGGER.atInfo().setMessage("Comment {} created successfully").addArgument(() -> comment.getId()).log();
        return comment;
    }

    @Async
    void sendMailToPostOwner(long postId, LocalDateTime date) {
        Post post;
        try {
            post = postService.getPostById(postId);
        } catch (NoSuchPostException e) {
            return;
        }
        Optional<User> user = userService.findById(post.getAuthor().getId());
        if (user.isEmpty())
            return;
        mailingService.sendNewCommentNotification(user.get(), post, date, Locale.of(user.get().getLocale()));
    }

    @Override
    public PaginatedDataWrapper<Comment> getPostCommentsPaginated(long postId, PaginationRequest request) {
        if (request.getPageSize() < 1) {
            throw new IllegalArgumentException("Invalid Page size");
        }
        if (postId < 1) {
            throw new IllegalArgumentException("Invalid Post id");
        }
        int totalCount = commentDao.getPostCommentsTotalCount(postId);
        if (request.getPageNumber() < 1) {
            throw new IllegalArgumentException("Invalid Page number");
        }
        int offset = (request.getPageNumber() - 1) * request.getPageSize();
        List<Comment> data = commentDao.getPostCommentsPaginated(postId, request.getPageSize(), offset);
        PaginatedDataWrapper<Comment> dataWrapper = new PaginatedDataWrapper<>(data, request.getPageNumber(),
                totalCount, request.getPageSize());
        if (request.getPageNumber() > dataWrapper.getTotalPages() && dataWrapper.getTotalPages() != 0) {
            throw new IllegalArgumentException("Invalid Page number");
        }

        return dataWrapper;
    }

    @Override
    @Transactional
    public GroovyCommentHistory giveGrooviness(long commentId, GroovyEnum value, long postId)
            throws NoSuchCommentException, NoLoggedUserException, NoSuchPostException, CommentAlreadyGroovedException,
            CommentIsDeletedException {

        Comment comment = commentDao.getCommentById(commentId).orElseThrow(NoSuchCommentException::new);
        if (comment.getDeleted()) {
            throw new CommentIsDeletedException();
        }
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        Post post = postService.getPostById(postId);
        if (groovyCommentHistoryService.findGroovyCommentHistory(user,
                comment, post).isPresent()) {
            throw new CommentAlreadyGroovedException();
        }

        commentDao.editGrooviness(comment, value);
        return groovyCommentHistoryService.createGroovyCommentHistory(user, comment, post, value);
    }

    @Override
    @Transactional
    public GroovyCommentHistory editGroovyness(long commentId, long postId, GroovyEnum value)
            throws NoLoggedUserException, NoSuchCommentException, NoSuchPostException, NoSuchGroovyCommentHistory,
            CommentIsDeletedException {
        Comment comment = commentDao.getCommentById(commentId).orElseThrow(NoSuchCommentException::new);
        if (comment.getDeleted()) {
            throw new CommentIsDeletedException();
        }
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        Post post = postService.getPostById(postId);
        GroovyCommentHistory groovyCommentHistory = groovyCommentHistoryService.findGroovyCommentHistory(user,
                comment, post).orElseThrow(NoSuchGroovyCommentHistory::new);
        GroovyEnum toUpdate = groovyCommentHistory.isGroovy() ? GroovyEnum.UP : GroovyEnum.DOWN;
        GroovyCommentHistory newHistory = null;
        switch (value) {
            case GroovyEnum.UP:
                if (toUpdate == GroovyEnum.DOWN) {
                    commentDao.editGrooviness(comment, GroovyEnum.UP_FROM_DOWN);
                    newHistory = groovyCommentHistoryService.updateGroovyCommentHistory(groovyCommentHistory,
                            GroovyEnum.UP);
                }
                break;
            case GroovyEnum.DOWN:
                if (toUpdate == GroovyEnum.UP) {
                    commentDao.editGrooviness(comment, GroovyEnum.DOWN_FROM_UP);
                    newHistory = groovyCommentHistoryService.updateGroovyCommentHistory(groovyCommentHistory,
                            GroovyEnum.DOWN);
                }

                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }

        return Objects.isNull(newHistory) ? groovyCommentHistory : newHistory;
    }

    @Transactional
    @Override
    public void deleteGroovyCommentHistory(long commentId, long postId)
            throws NoSuchCommentException, NoSuchGroovyCommentHistory, NoSuchPostException, NoLoggedUserException,
            CommentIsDeletedException {
        Comment comment = commentDao.getCommentById(commentId).orElseThrow(NoSuchCommentException::new);
        if (comment.getDeleted()) {
            throw new CommentIsDeletedException();
        }
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        Post post = postService.getPostById(postId);
        GroovyCommentHistory groovyCommentHistory = groovyCommentHistoryService.findGroovyCommentHistory(user,
                comment, post).orElseThrow(NoSuchGroovyCommentHistory::new);

        groovyCommentHistoryService.deleteGroovyCommentHistory(user, comment);
        commentDao.editGrooviness(comment, groovyCommentHistory.isGroovy() ? GroovyEnum.DOWN : GroovyEnum.UP);
    }

    @Override
    public Optional<GroovyCommentHistory> findGroovyCommentHistory(long commentId, long postId)
            throws NoSuchCommentException, NoSuchPostException, NoLoggedUserException {
        Comment comment = commentDao.getCommentById(commentId).orElseThrow(NoSuchCommentException::new);
        Post post = postService.getPostById(postId);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);
        return groovyCommentHistoryService.findGroovyCommentHistory(user,
                comment, post);

    }

    @Transactional
    @Override
    public void editGroovinessOnComment(long commentId, int grooviness, long postId)
            throws NoSuchCommentException, NoLoggedUserException, NoSuchPostException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        User user = userService.getLoggedUser().orElseThrow(NoLoggedUserException::new);

        GroovyEnum groovyValue = GroovyEnum.fromValue(grooviness);
        if (!GroovyEnum.isValidInput(grooviness) || groovyValue == null) {
            throw new IllegalArgumentException("Invalid grooviness value");
        }

        if (comment.isEmpty())
            throw new NoSuchCommentException();
        // checks whether the user has already grooved the comment
        Post post = postService.getPostById(postId);

        Optional<GroovyCommentHistory> maybeGCH = groovyCommentHistoryService.findGroovyCommentHistory(user,
                comment.get(), post);
        if (maybeGCH.isEmpty()) {
            groovyCommentHistoryService.createGroovyCommentHistory(user, comment.get(), post, groovyValue);
            commentDao.editGrooviness(comment.get(), groovyValue);
            return;
        }

        GroovyEnum groovy = maybeGCH.get().isGroovy() ? GroovyEnum.UP : GroovyEnum.DOWN;
        switch (groovyValue) {
            case GroovyEnum.UP:
                if (groovy == GroovyEnum.UP) {
                    groovyCommentHistoryService.deleteGroovyCommentHistory(user, comment.get());
                    commentDao.editGrooviness(comment.get(), GroovyEnum.DOWN);
                } else {
                    commentDao.editGrooviness(comment.get(), GroovyEnum.UP_FROM_DOWN);
                    groovyCommentHistoryService.updateGroovyCommentHistory(maybeGCH.get(), GroovyEnum.UP);
                }
                break;
            case GroovyEnum.DOWN:
                if (groovy == GroovyEnum.UP) {
                    commentDao.editGrooviness(comment.get(), GroovyEnum.DOWN_FROM_UP);
                    groovyCommentHistoryService.updateGroovyCommentHistory(maybeGCH.get(), GroovyEnum.DOWN);
                } else {
                    groovyCommentHistoryService.deleteGroovyCommentHistory(user, comment.get());
                    commentDao.editGrooviness(comment.get(), GroovyEnum.UP);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid grooviness value");
        }
    }

    @Override
    public List<Comment> getUpGroovedComments(long postId) throws UserNotFoundException, NoSuchPostException {
        Optional<User> possibleUser = userService.getLoggedUser();
        if (possibleUser.isEmpty())
            throw new UserNotFoundException();
        User user = possibleUser.get();
        Post post = postService.getPostById(postId);
        return commentDao.getGroovedComments(post.getId(), user.getId());
    }

    @Override
    public List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException {
        Optional<User> possibleUser = userService.getLoggedUser();
        if (possibleUser.isEmpty()) {
            LOGGER.atError().setMessage("Logged user not found").log();
            throw new UserNotFoundException();
        }
        User user = possibleUser.get();
        return commentDao.getDownGroovedComments(postId, user.getId());
    }

    @Transactional
    @Override
    public int deleteComment(long commentId)
            throws NoSuchCommentException, NoSuchPostException, PostIsDeletedException {
        Optional<Comment> comment = commentDao.getCommentById(commentId);
        if (comment.isEmpty()) {
            LOGGER.atError().setMessage("Comment with id {} not found").addArgument(commentId).log();
            throw new NoSuchCommentException();
        }
        Post post = postService.getPostById(comment.get().getPostId());
        if (post.getDeleted()) {
            LOGGER.atError().setMessage("Post with id {} is deleted").addArgument(() -> comment.get().getPostId())
                    .log();
            throw new PostIsDeletedException();
        }
        int ret = commentDao.deleteComment(comment.get());
        notifyCommentDeletion(comment.get());
        LOGGER.atInfo().setMessage("Comment {} deleted successfully").addArgument(() -> comment.get().getId()).log();
        return ret;
    }

    @Async
    public void notifyCommentDeletion(Comment comment) {
        Optional<User> user = userService.findByUsername(comment.getUsername());
        if (user.isEmpty()) {
            LOGGER.atWarn().setMessage("User not found with username {}").addArgument(() -> comment.getUsername())
                    .log();
            return;
        }
        Post post;
        try {
            post = postService.getPostById(comment.getPostId());
        } catch (NoSuchPostException e) {
            LOGGER.atWarn().setMessage("Post with id {} is deleted").addArgument(() -> comment.getPostId()).log();
            return;
        }
        mailingService.notifyCommentDeletion(user.get().getEmail(), user.get().getUsername(), comment.getPostId(),
                post.getTitle(), post.getcommunity().getName(), comment.getBody(), Locale.of(user.get().getLocale()));
    }
}
