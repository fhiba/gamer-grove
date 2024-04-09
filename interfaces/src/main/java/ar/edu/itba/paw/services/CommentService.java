package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoSuchCommentException;
import ar.edu.itba.paw.exceptions.NoSuchPostException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Comment;

import java.util.List;

public interface CommentService {
        Comment createComment(long postId, String body);
        List<Comment> getPostComments(long postId);
        void editGroovinessOnComment(long commentId, int grooviness, long postId) throws NoSuchCommentException, UserNotFoundException;

        List<Comment> getUpGroovedComments(long postId) throws NoSuchPostException, UserNotFoundException;

        List<Comment> getDownGroovedComments(long postId) throws UserNotFoundException;
}
