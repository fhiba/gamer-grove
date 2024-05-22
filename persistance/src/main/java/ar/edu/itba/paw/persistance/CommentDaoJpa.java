package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.GroovyCommentHistory;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Primary
public class CommentDaoJpa implements CommentDao{
    @PersistenceContext
    private EntityManager em;
    @Override
    public Comment createComment(Post post, String body, User user, LocalDateTime dateTime, long userId) {
        Comment comment = new Comment(post,user,body,dateTime,0,false);
        em.persist(comment);
        return comment;
    }


    @Override
    public List<Comment> getPostComments(long postId) {
        TypedQuery<Comment> query = em.createQuery("from Comment as c where c.post.id = :postId", Comment.class);
        query.setParameter("postId", postId);
        return query.getResultList();


    }

    @Override
    public Optional<Comment> getCommentById(long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));

    }


    @Override
    public void editGrooviness(Comment comment, int i) {
        comment.setGrooviness(comment.getGrooviness()+i);
        em.merge(comment);
    }

    @Override
    public Optional<Boolean> getGroovyTypeFromComment(Comment comment, User user, Post post) {
        GroovyCommentHistory.GroovyCommentHistoryId groovyCommentHistoryId = new GroovyCommentHistory.GroovyCommentHistoryId(user,comment,post);
        return Optional.of(em.find(GroovyCommentHistory.class, groovyCommentHistoryId).isGrooviness());
    }

    @Override
    public void insertGroovinessIntoComment(Comment comment, User user, Post post, boolean grooviness) {
        GroovyCommentHistory gch = new GroovyCommentHistory(user,comment,post,grooviness);
        em.persist(gch);
    }

    @Override
    public void deleteGrooviness(Post post, User user, Comment comment) {
        em.createNativeQuery("DELETE FROM groovy_comment_history WHERE comment_id = ? AND user_id = ? AND post_id = ?")
                .setParameter(1,comment.getId())
                .setParameter(2,user.getId())
                .setParameter(3,post.getId())
                .executeUpdate();
    }

    @Override
    public void updateGroovyHistory(Post post, User user, Comment comment, boolean grooviness) {
        em.createNativeQuery("UPDATE groovy_comment_history SET groovy_type = ? WHERE post_id = ? AND comment_id = ? AND user_id = ?")
                .setParameter(1,grooviness)
                .setParameter(2,post.getId())
                .setParameter(3,comment.getId())
                .setParameter(4,user.getId())
                .executeUpdate();
    }

    @Override
    public List<Comment> getGroovedComments(long postId, long id) {
        return em.createNativeQuery("SELECT comment.* from comment join groovy_comment_history on comment.post_id = groovy_comment_history.post_id and comment.id = groovy_comment_history.comment_id where comment.post_id = ? and user_id = ? and groovy_type = true ", Comment.class)
                .setParameter(1,postId)
                .setParameter(2,id)
                .getResultList();

    }

    @Override
    public List<Comment> getDownGroovedComments(long postId, long id) {
        return em.createNativeQuery("SELECT comment.* from comment join groovy_comment_history on comment.post_id = groovy_comment_history.post_id and comment.id = groovy_comment_history.comment_id where comment.post_id = ? and user_id = ? and groovy_type = false ", Comment.class)
                .setParameter(1,postId)
                .setParameter(2,id)
                .getResultList();    }

    @Override
    public int deleteComment(Comment comment) {
        comment.setDeleted(true);
        em.merge(comment);
        return 1;
        //FIXME
    }

    @Override
    public int getPostCommentsTotalCount(long postId) {
        String hql = "SELECT COUNT(c) FROM Comment c WHERE  c.post.id = :postId";
        Query query = em.createQuery(hql);
        query.setParameter("postId", postId);
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public List<Comment> getPostCommentsPaginated(long postId, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM comment WHERE post_id = :postId ORDER BY comment_date DESC");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("postId", postId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Comment> query = em.createQuery("from Comment as c where c.id IN :ids", Comment.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }
}
