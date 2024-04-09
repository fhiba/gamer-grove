package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
        Comment createComment(long postId, String body);
        List<Comment> getPostComments(long postId);
}
