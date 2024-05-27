package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface GroovyCommentHistoryDao {

    GroovyCommentHistory createGroovyCommentHistory(User user, Comment comment, Post post, Boolean groovy);

    void deleteGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory);

    Optional<GroovyCommentHistory> findGroovyCommentHistory(User user, Comment comment, Post post);

    GroovyCommentHistory updateGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory, Boolean newValue);
}
