package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Primary
public class UserDaoJPA implements UserDao {

    @PersistenceContext
    private EntityManager em;
    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class,id));
    }

    @Override
    public Optional<User> findByEmail(String mail) {
        TypedQuery<User> query = em.createQuery("from User where email = :email",User.class);
        query.setParameter("email",mail);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = em.createQuery("from User where username = :username",User.class);
        query.setParameter("username",username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public User create(String username, String email, String password) {
        User newUser = new User(username,password,email,false,"en",false);
        em.persist(newUser);
        return newUser;
    }

    @Override
    public User updateImage(User user, File image) {
        user.setImage(image);
        return em.merge(user);
    }

    @Override
    public List<User> getFollowersOfCommunity(long communityId) {
        Query list = em.createNativeQuery("SELECT users.id FROM users WHERE id IN (SELECT user_id FROM community_user WHERE community_id = :communityId)")
                .setParameter("communityId",communityId);

        List<Long> resultList = ((Stream<Number>) list.getResultStream()).map(Number::longValue).toList();
        TypedQuery<User> query = em.createQuery("from User where id in :list",User.class);
        query.setParameter("list",resultList);
        return query.getResultList();
    }

    @Override
    public List<User> getUsers(int pageSize, int offset) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM users");
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);

        List<Long> resultList = ((Stream<Number>) nativeQuery.getResultStream()).map(Number::longValue).toList();

        TypedQuery<User> query = em.createQuery("from User as u where u.id IN :ids", User.class);
        query.setParameter("ids", resultList);
        return query.getResultList();
    }

    @Override
    public int getUsersCount() {
        String hql = "SELECT COUNT(u) FROM User u";
        Query query = em.createQuery(hql);
        return ((Long) query.getSingleResult()).intValue();
    }



    @Override
    public User updatePassword(User user, String password) {
        user.setPassword(password);
        return em.merge(user);
    }

    @Override
    public User verifyUser(User user) {
        user.setVerified(true);
        return em.merge(user);
    }

    @Override
    public User updateLocale(User user, String locale) {
        user.setLocale(locale);
        return em.merge(user);
    }

}
