package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;

import java.util.List;

public interface CommentService {
        Comment createComment(long postId, String body) throws NoLoggedUserException, NoSuchPostException, PostIsDeletedException;
        List<Comment> getPostComments(long postId);

        PaginatedDataWrapper<Comment> getPostCommentsPaginated(long postId, PaginationRequest request);


        void editGroovinessOnComment(long commentId, int grooviness, long postId) throws NoSuchCommentException, UserNotFoundException, NoLoggedUserException;

        List<Comment> getUpGroovedComments(long postId) throws NoSuchPostException, UserNotFoundException;

        List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException;

        int deleteComment(long commentId) throws NoSuchCommentException, NoSuchPostException, PostIsDeletedException;
}
