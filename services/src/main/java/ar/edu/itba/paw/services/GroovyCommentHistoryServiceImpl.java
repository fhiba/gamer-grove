package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.GroovyCommentHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Transactional(readOnly = true)
@Service
public class GroovyCommentHistoryServiceImpl implements GroovyCommentHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyCommentHistoryServiceImpl.class);

    @Autowired
    private GroovyCommentHistoryDao gchDao;

    @Transactional
    @Override
    public GroovyCommentHistory createGroovyCommentHistory(User user, Comment comment, Post post, Boolean groovy) {
        return gchDao.createGroovyCommentHistory(user, comment, post, groovy);
    }

    @Transactional
    @Override
    public void deleteGroovyCommentHistory(User user, Comment comment) {
        Optional<GroovyCommentHistory> maybeGCH = gchDao.findGroovyCommentHistory(user, comment, comment.getPost());

        if(maybeGCH.isEmpty()){
            LOGGER.atWarn().setMessage("Can not delete the upvote of the user {} with the comment {} because does not exists").addArgument(()->user.getUsername()).addArgument(()->comment.getId()).log();
        }
        if(maybeGCH.isPresent()){
            LOGGER.atInfo().setMessage("Correctly delete the upvote of the user {} with the comment {}").addArgument(()->user.getUsername()).addArgument(()->comment.getId()).log();
            gchDao.deleteGroovyCommentHistory(maybeGCH.get());
        }
    }

    @Override
    public Optional<GroovyCommentHistory> findGroovyCommentHistory(User user, Comment comment, Post post) {
        return gchDao.findGroovyCommentHistory(user, comment, post);
    }

    @Transactional
    @Override
    public GroovyCommentHistory updateGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory, Boolean newValue) {

        return gchDao.updateGroovyCommentHistory(groovyCommentHistory, newValue);
    }
}
