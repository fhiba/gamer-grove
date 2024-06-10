package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger LOGGER = LoggerFactory.getLogger(PostDaoJpa.class);
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
        final Post post = new Post(title, body, author, community, media, null, now, 0, false, category);
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
    public void addToGroovy(User user, Post post, boolean grooviness) {
        GroovyPostHistory gph = new GroovyPostHistory(user, post, grooviness);
        em.persist(gph);
    }

    @Override
    public void addToGroovy(long userId, long postId, boolean grooviness) {
/*
       GroovyPostHistory gph = new GroovyPostHistory((int) userId, (int) id, grooviness);
       em.persist(gph);
*/
    }

    @Override
    public Optional<Boolean> checkGrooviness(long postId, long userId) {
        @SuppressWarnings("unchecked")
        Optional<Boolean> result = em.createNativeQuery("SELECT groovy_type FROM groovy_post_history WHERE post_id = :postId AND user_id = :userId")
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getResultList().stream().findFirst();
        return result;
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
//        return em.createNativeQuery("SELECT p.category FROM post as p WHERE p.category is not null GROUP BY p.category").getResultList();
//        String hql = "SELECT Distinct category from Post where deleted = false and category IS NOT NULL";
//        TypedQuery<PostCategories> query = em.createQuery(hql, PostCategories.class);
//        return query.getResultList();
//        return em.createQuery("SELECT p.category FROM Post as p WHERE p.deleted = false GROUP BY p.category", PostCategories.class).getResultList();
        String sql = "SELECT p.category FROM post p WHERE p.category IS NOT NULL AND deleted=false GROUP BY p.category";
        List<String> categoryStrings = em.createNativeQuery(sql).getResultList();

        // Print out the categoryStrings list

        // Convert the list of strings to a list of PostCategory enums
//        List<PostCategories> categories = categoryStrings.stream()
//                .map(PostCategories::valueOf)
//                .collect(Collectors.toList());
        return categoryStrings;
        // Native SQL query to select the category field from the Post entity

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
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc ", Post.class);
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
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("category",category);
        nativeQuery.setMaxResults(pageSize);
        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc ", Post.class);
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
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("name",communityName);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc", Post.class);
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
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false AND community_name IN (SELECT community_name FROM community_user WHERE user_id = :userId) ORDER BY post_date DESC");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("userId",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc", Post.class);
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
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post WHERE deleted = false AND category = :category AND community_name IN (SELECT community_name FROM community_user WHERE user_id = :userId) ORDER BY post_date DESC");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("category",category);
        nativeQuery.setParameter("userId",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc ", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalUserLikedPostCount(long userId) {
        String sql = "SELECT COUNT(*) FROM groovy_post_history WHERE user_id = :userId and groovy_type = true";
        Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userId);
        LOGGER.info("Total user liked posts: {}" , ((Number) query.getSingleResult()).intValue());
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Post> getUserLikedPostPaginated(long userId, int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT post_id FROM groovy_post_history JOIN post ON groovy_post_history.post_id = post.id WHERE user_id = :userId and groovy_type = true ORDER BY post_date DESC");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setParameter("userId",userId);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc", Post.class);
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
        Query nativeQuery = em.createNativeQuery("SELECT id FROM post where author_id = :authorId ORDER BY post_date DESC");
        nativeQuery.setParameter("authorId",id);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);
        List<Long> resultList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();
        TypedQuery<Post> query = em.createQuery("from Post as p where p.id IN :ids order by p.date desc", Post.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getTotalPostsByUser(long id) {
        Query query= em.createQuery("select count(*) from Post as p where p.author.id = :id")
                .setParameter("id", id);
        return((Number) query.getSingleResult()).intValue();
    }

    @Override
    public void removePost(Post post) {
        post.setDeleted(true);
    }

    @Override
    public List<Post> topFivePosts() {
        TypedQuery<Post> query = em.createQuery("from Post as p where p.deleted = false order by p.grooviness desc", Post.class);
        query.setMaxResults(5);
        return query.getResultList();
    }

    private class QueryBuilder {
        private static final String COUNT = "SELECT COUNT(post.id) FROM post WHERE post.deleted = false ";
        private static final String SELECT = "SELECT post.id FROM post WHERE post.deleted = false ";
        private static final String COMMUNITY_FILTERED = " AND post.community_name IN (SELECT cu.community_name FROM community_user AS cu WHERE cu.user_id = :userId)";
        private static final String CATEGORY = " AND post.category = :category";
        private static final String DEFAULT_ORDER_BY = " ORDER BY post.post_date DESC";
        private static final String OLDEST_ORDER_BY = " ORDER BY post.post_date ASC";
        private static final String HOTTEST_ORDER_BY = " ORDER BY post.grooviness DESC";
        private static final String DEFAULT_RETRIEVE = "from Post as p where p.id IN :ids order by p.date desc";
        private static final String OLD_RETRIEVE = "from Post as p where p.id IN :ids order by p.date asc";
        private static final String HOTTEST_RETRIEVE = "from Post as p where p.id IN :ids order by p.grooviness desc";
        private PostCategories category;
        private PostOrders order;
        private Long userId;

         public QueryBuilder() {

        }

        public QueryBuilder forUser(Long userId) {
            this.userId = userId;
            return this;
        }

        public QueryBuilder fromCategory(PostCategories category) {
            this.category = category;
            return this;
        }

        public QueryBuilder orderBy(PostOrders order) {
            this.order = order;
            return this;
        }

        List<Long> getIds(Integer pageSize, Integer offset) {
            StringBuilder queryString = new StringBuilder(SELECT);
            if (category != null) {
                queryString.append(CATEGORY);
            }
            if (userId != null) {
                queryString.append(COMMUNITY_FILTERED);
            }
            switch (order) {

                case OLDEST:
                    queryString.append(OLDEST_ORDER_BY);
                    break;
                case HOTTEST:
                    queryString.append(HOTTEST_ORDER_BY);
                    break;
                case NEWEST:
                case DEFAULT:
                    queryString.append(DEFAULT_ORDER_BY);
                    break;
            }

            LOGGER.debug("Query: {}", queryString.toString());
            Query nativeQuery = em.createNativeQuery(queryString.toString());
            if (category != null) {
                nativeQuery.setParameter("category", category.getCategory());
            }
            if (userId != null) {
                nativeQuery.setParameter("userId", userId);
            }
            nativeQuery.setFirstResult(offset);
            nativeQuery.setMaxResults(pageSize);
            return ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();
        }

        List<Post> build(Integer pageSize, Integer offset) {
             List<Long> ids = getIds(pageSize, offset);
             String query;
             switch (order) {
                 case OLDEST:
                     query = OLD_RETRIEVE;
                     break;
                 case HOTTEST:
                     query = HOTTEST_RETRIEVE;
                     break;
                 case NEWEST:
                 case DEFAULT:
                 default:
                     query = DEFAULT_RETRIEVE;
                     break;
             }
                TypedQuery<Post> typedQuery = em.createQuery(query, Post.class);
                typedQuery.setParameter("ids", ids);
                return typedQuery.getResultList();
        }

        Integer buildCount() {
            StringBuilder queryString = new StringBuilder(COUNT);
            if (category != null) {
                queryString.append(CATEGORY);
            }
            if (userId != null) {
                queryString.append(COMMUNITY_FILTERED);
            }
            LOGGER.debug("Count Query: {}", queryString.toString());
            Query query = em.createNativeQuery(queryString.toString());
            if (category != null) {
                query.setParameter("category", category.getCategory());
            }
            if (userId != null) {
                query.setParameter("userId", userId);
            }
            return ((Number) query.getSingleResult()).intValue();
        }
    }

    public List<Post> find(int pageSize, int offset, PostCategories category, PostOrders order, Long userId) {
         return new QueryBuilder()
                .fromCategory(category)
                .orderBy(order)
                .forUser(userId)
                .build(pageSize, offset);
    }

    public Integer findCount(PostCategories category, Long userId) {
        return new QueryBuilder()
                .fromCategory(category)
                .forUser(userId)
                .buildCount();
    }
}
