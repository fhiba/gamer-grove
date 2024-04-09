package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Comment createComment(long postId, String body,String username, LocalDateTime dateTime, long userId);

    List<Comment> getPostComments(long postId);

    Optional<Comment> getCommentById(long commentId);

    void editGrooviness(long commentId, int i);

    Optional<Boolean> getGroovyTypeFromComment(long commentId, long id, long postId);

    void insertGroovinessIntoComment(long commentId, long id, long postId, boolean grooviness);

    void deleteGrooviness(long commentId, long id, long postId);

    List<Comment> getGroovedComments(long postId, long id);

    List<Comment> getDownGroovedComments(long postId, long id);



}
