package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Primary
public class GroovyCommentHistoryDaoJpa implements GroovyCommentHistoryDao {

    @PersistenceContext
    private EntityManager em;
    @Override
    public GroovyCommentHistory createGroovyCommentHistory(User user, Comment comment, Post post, Boolean groovy) {
        GroovyCommentHistory gch = new GroovyCommentHistory(user, comment, post, groovy);
        em.persist(gch);
        return gch;
    }

    @Override
    public void deleteGroovyCommentHistory(GroovyCommentHistory ghc) {
        em.remove(ghc);
    }

    @Override
    public Optional<GroovyCommentHistory> findGroovyCommentHistory(User user, Comment comment, Post post) {

        GroovyCommentHistory.GroovyCommentHistoryId id = new GroovyCommentHistory.GroovyCommentHistoryId(user, comment, post);
        return em.createQuery("from GroovyCommentHistory as gch where gch.user = :user AND gch.comment = :comment AND gch.post = :post", GroovyCommentHistory.class)
                        .setParameter("user", user)
                        .setParameter("comment", comment)
                        .setParameter("post", post)
                .getResultStream().findFirst();
    }

    @Override
    public GroovyCommentHistory updateGroovyCommentHistory(GroovyCommentHistory groovyCommentHistory, Boolean newValue) {
        groovyCommentHistory.setGroovy(newValue);
        return em.merge(groovyCommentHistory);
    }


}
