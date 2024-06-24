package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;

import java.util.Optional;

public interface GroovyCommentHistoryService {
    GroovyCommentHistory createGroovyCommentHistory(User user, Comment comment, Post post, GroovyEnum groovyEnum);

    void deleteGroovyCommentHistory(User user, Comment comment);

    Optional<GroovyCommentHistory> findGroovyCommentHistory(User user, Comment comment, Post post);

    GroovyCommentHistory updateGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory, GroovyEnum groovyEnum);
}
