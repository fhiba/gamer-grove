package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface GroovyCommentHistoryService {
    GroovyCommentHistory createGroovyCommentHistory(User user, Comment comment, Post post, Boolean groovy);

    void deleteGroovyCommentHistory(User user, Comment comment);

    Optional<GroovyCommentHistory> findGroovyCommentHistory(User user, Comment comment, Post post);

    GroovyCommentHistory updateGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory, Boolean newValue);
}
