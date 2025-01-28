package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.GroovyEnum;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment createComment(long postId, String body)
            throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException;

    PaginatedDataWrapper<Comment> getPostCommentsPaginated(long postId, PaginationRequest request);

    void editGroovinessOnComment(long commentId, int grooviness, long postId)
            throws NoSuchCommentException, UserNotFoundException, NoLoggedUserException, NoSuchPostException;

    List<Comment> getUpGroovedComments(long postId) throws NoSuchPostException, UserNotFoundException;

    List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException;

    int deleteComment(Long commentId, Long postId)
            throws NoSuchCommentException, NoSuchPostException, PostIsDeletedException, CommentIsDeletedException;

    GroovyCommentHistory giveGrooviness(long commentId, GroovyEnum value, long postId)
            throws NoSuchCommentException, NoLoggedUserException, NoSuchPostException, CommentAlreadyGroovedException,
            CommentIsDeletedException;

    GroovyCommentHistory editGroovyness(long commentId, long postId, GroovyEnum value)
            throws NoLoggedUserException, NoSuchCommentException, NoSuchPostException, NoSuchGroovyCommentHistory,
            CommentIsDeletedException;

    void deleteGroovyCommentHistory(long commentId, long postId)
            throws NoSuchCommentException, NoSuchGroovyCommentHistory, NoSuchPostException, NoLoggedUserException,
            CommentIsDeletedException;

    Optional<GroovyCommentHistory> findGroovyCommentHistory(long commentId, long postId)
            throws NoSuchCommentException, NoSuchPostException, NoLoggedUserException;
}
