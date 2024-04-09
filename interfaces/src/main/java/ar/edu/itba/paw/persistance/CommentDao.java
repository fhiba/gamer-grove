package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentDao {
    Comment createComment(long postId, String body,String username, LocalDateTime dateTime, long userId);
    List<Comment> getPostComments(long postId);
}
