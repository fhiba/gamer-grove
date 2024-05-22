package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.Community;

import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
@Primary
public class ModderDaoJpa implements ModderDao{
    @PersistenceContext
    private EntityManager em;

    @Override
    public int addModder(long userId, long communityId) {
        String sql = "INSERT INTO modders (user_id, community_id) VALUES (?, ?)";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, communityId);
        return query.executeUpdate();
    }

    @Override
    public boolean isModderOfCommunity(long userId, long communityId) {
        String sql = "SELECT COUNT(*) FROM modders WHERE user_id = ? AND community_id = ?";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, communityId);
        int count = ((Number) query.getSingleResult()).intValue();
        return count > 0;
    }

    @Override
    public int removeModder(long userId, long communityId) {
        String sql = "DELETE FROM modders WHERE user_id = ? AND community_id = ?";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, userId);
        query.setParameter(2, communityId);
        return query.executeUpdate();
    }

    @Override
    public int removePost(long postId) {
        String sql = "DELETE FROM post WHERE id = ?";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, postId);
        return query.executeUpdate();
    }
}
