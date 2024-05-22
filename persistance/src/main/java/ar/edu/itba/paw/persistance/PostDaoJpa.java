package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Primary
public class PostDaoJpa implements PostDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Post> findById(long id) {
        return Optional.ofNullable(em.find(Post.class, id));

    }

    @Override
    public List<Post> findAllPosts() {
        TypedQuery<Post> query = em.createQuery("from Post", Post.class);
        return query.getResultList();
    }

    @Override
    public Post createPost(String title, String body, User author, Community community, boolean media, LocalDateTime now, String category) {
        final Post post = new Post(title, body, author, community, media, null, now, 0, false, PostCategories.valueOf(category));
        em.persist(post);
        return post;
    }

    @Override
    public List<Post> findPostsByCommunity(String communityId) {
        TypedQuery<Post> query = em.createQuery("from Post as p where p.community.name = :communityId", Post.class);
        query.setParameter("communityId", communityId);
        return query.getResultList();
    }

    @Override
    public List<Post> findByCategory(String category) {
        TypedQuery<Post> query = em.createQuery("from Post as p where p.category = :category", Post.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public void editGrooviness(long postId, int i) {
        em.createNativeQuery("UPDATE post SET grooviness = grooviness + :i WHERE id = :id")
                .setParameter("id", postId)
                .setParameter("i", i)
                .executeUpdate();

    }

    @Override
    public void addToGroovy(long userId, long postId, boolean grooviness) {
//        GroovyPostHistory gph = new GroovyPostHistory((int) post.getAuthor().getId(), (int) id, grooviness);
//        em.persist(gph);
    }

    @Override
    public Optional<Boolean> checkGrooviness(long postId, long userId) {
        return em.createNativeQuery("SELECT groovy_type FROM groovy_post_history WHERE post_id = :postId AND user_id = :userId", Boolean.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getResultList().stream().findFirst();
    }

    @Override
    public void insertIntoGroovyHistory(long postId, long id, boolean grooviness) {

    }

//    @Override
//    public void insertIntoGroovyHistory(Post post, long id, boolean grooviness) {
//        GroovyPostHistory gph = new GroovyPostHistory((int) post.getAuthor().getId(), (int) id, grooviness);
//        em.persist(gph);
//    }

    @Override
    public void deleteGrooviness(long postId, long id) {
        em.createNativeQuery("DELETE FROM groovy_post_history where post_id = :post_id and user_id = :id")
                .setParameter("post_id",postId)
                .setParameter("id",id)
                .executeUpdate();
    }

    @Override
    public void updateGroovyHistory(long postId, long id, boolean b) {
        em.createNativeQuery("UPDATE groovy_post_history set groovy_type = :b where post_id =:postId and user_id = :id")
                .setParameter("b",b)
                .setParameter("postId",postId)
                .setParameter("id",id)
                .executeUpdate();
    }

    @Override
    public Optional<Post> findByIdWithImage(long id) {
        return Optional.empty();
    }

    @Override
    public List<Post> getMyFollowedPosts(long userId) {
        String sql = "SELECT * FROM post WHERE community_name IN (SELECT community_name FROM community_user WHERE user_id = ?) AND deleted = false ORDER BY post_date DESC";
        Query query = em.createNativeQuery(sql, Post.class);
        query.setParameter(1, userId);
        return query.getResultList();
    }

    @Override
    public List<Post> getMyFollowedPostsByCategory(String category, long userId) {
        String sql = "SELECT * FROM post WHERE community_name IN (SELECT community_name FROM community_user WHERE user_id = ?) AND category = ? AND deleted = false ORDER BY post_date DESC";
        Query query = em.createNativeQuery(sql, Post.class);
        query.setParameter(1, userId);
        query.setParameter(2, category);
        return query.getResultList();
    }

    @Override
    public List<Post> findPostsByUser(long id) {
        TypedQuery<Post> query = em.createQuery("from Post as p where p.author.id = :id", Post.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    public List<Post> findPostsLikedByUser(long id) {
        String sql = "SELECT * FROM post WHERE id IN(SELECT post_id FROM groovy_post_history WHERE user_id=?) ORDER BY post_date DESC";
        Query query = em.createNativeQuery(sql, Post.class);
        query.setParameter(1, id);
        return null;
    }

    @Override
    public List<String> getUsedCategories() {
        String hql = "SELECT DISTINCT p.category FROM Post p WHERE p.category IS NOT NULL";
        TypedQuery<String> query = em.createQuery(hql, String.class);
        return query.getResultList();

    }

    @Override
    public int getTotalPostCount() {
        String hql = "SELECT COUNT(p) FROM Post p WHERE p.deleted = false";
        Query query = em.createQuery(hql);
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getAllPostsPaginated(int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false ORDER BY post_date DESC");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalPostByCategoryCount(String category) {

        Query query = em.createQuery("SELECT COUNT(*) FROM Post WHERE deleted = false AND category = :category");
        query.setParameter("category", category);
        return ((Long) query.getSingleResult()).intValue();

    }

    @Override
    public List<Post> getAllPostsByCategoryPaginated(String category, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false and category = :category ORDER BY post_date DESC");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("category",category);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalPostByCommunityCount(String communityName) {
        Query query = em.createQuery("SELECT COUNT(*) FROM Post WHERE deleted = false AND community.name = :name");
        query.setParameter("name", communityName);
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getPostsByCommunityPaginated(String communityName, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false and community_name = :name ORDER BY post_date DESC");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("name",communityName);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotaltFollowedPostsByUserCount(long userId) {
        String sql = "SELECT COUNT(*) FROM post WHERE deleted = false AND community_name IN (SELECT community_name FROM community_user WHERE user_id = ?)";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, userId);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getFollowedPostsByUserPaginated(long userId, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false AND community_name IN (SELECT community_name FROM community_user WHERE user_id = :userId)");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("userId",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalUserFollowedPostsByCategoryCount(long userId, String category) {
        String sql = "SELECT COUNT(*) FROM post WHERE deleted = false AND category = ? AND community_name IN (SELECT community_name FROM community_user WHERE user_id = ?)";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, category);
        query.setParameter(2, userId);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getUserFollowedPostsByCategoryPaginated(long userId, String category, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false AND category = :category AND community_name IN (SELECT community_name FROM community_user WHERE user_id = :userId)");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("category",category);
        nativeQuery.setParameter("userId",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalUserLikedPostCount(long userId) {
        String sql = "SELECT COUNT(*) FROM groovy_post_history WHERE user_id = ? and groovy_type = true";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, userId);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getUserLikedPostPaginated(long userId, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT post_id FROM groovy_post_history WHERE user_id = ? and groovy_type = true");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("1",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public List<Post> getNewsLimited(int limit) {
        String hql = "FROM Post p WHERE p.category = 'News' AND p.deleted = false ORDER BY p.date DESC";
        TypedQuery<Post> query = em.createQuery(hql, Post.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Post> getPostsByUserPaginated(long id, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post where author_id = ?");
        nativeQuery.setFirstResult(pageSize * ((offset/pageSize)));
        nativeQuery.setParameter("1",id);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalPostsByUser(long id) {
        String sql = "SELECT COUNT(*) FROM post where author_id = ?";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1, id);
        return ((Number) query.getSingleResult()).intValue();
    }
}
