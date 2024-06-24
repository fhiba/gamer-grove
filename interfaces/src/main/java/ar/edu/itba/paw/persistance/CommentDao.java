package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Comment createComment(Post post, String body, User user, LocalDateTime dateTime, long userId);

    List<Comment> getPostComments(long postId);

    Optional<Comment> getCommentById(long commentId);

    void editGrooviness(Comment comment, GroovyEnum groovyEnum);

    void insertGroovinessIntoComment(Comment comment, User user, Post post, boolean grooviness);

    List<Comment> getGroovedComments(long postId, long id);

    List<Comment> getDownGroovedComments(long postId, long id);

    int deleteComment(Comment comment);

    int getPostCommentsTotalCount(long postId);

    List<Comment> getPostCommentsPaginated(long postId, int pageSize, int offset);


}
