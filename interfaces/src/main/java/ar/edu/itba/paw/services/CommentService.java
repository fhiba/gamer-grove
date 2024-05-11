package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.exceptions.NoSuchCommentException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.pagination.PaginatedDataWrapper;
import ar.edu.itba.paw.models.pagination.PaginationRequest;

import java.util.List;

public interface CommentService {
        Comment createComment(long postId, String body) throws NoLoggedUserException;
        List<Comment> getPostComments(long postId);

        PaginatedDataWrapper<Comment> getPostCommentsPaginated(long postId, PaginationRequest request);


        void editGroovinessOnComment(long commentId, int grooviness, long postId) throws NoSuchCommentException, UserNotFoundException;

        List<Comment> getUpGroovedComments(long postId) throws NoSuchPostException, UserNotFoundException;

        List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException;

        public int deleteComment(long commentId) throws NoSuchCommentException;
}
