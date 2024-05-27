package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

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
    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("from User",User.class);
        return query.getResultList();
    }

    @Override
    public User updateImage(User user, File image) {
        user.setImage(image);
        return em.merge(user);
    }

    @Override
    public List<User> getFollowersOfCommunity(long communityId) {
        @SuppressWarnings("unchecked")
        List<User> list = em.createNativeQuery("SELECT * FROM users WHERE id IN (SELECT user_id FROM community_user WHERE community_id = :communityId)")
                .setParameter("communityId",communityId)
                .getResultList();
        return list;
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


//    @Override
//    public void updateImageId(long id, long imageId) {
//         em.createNativeQuery("UPDATE users SET portrait_id = ? WHERE id=?")
//                 .setParameter(1,imageId)
//                 .setParameter(2,id)
//                 .executeUpdate();
//    }
//
//    @Override
//    public void updatePassword(Long id, String password) {
//        em.createNativeQuery("UPDATE users SET password = ? WHERE id=?")
//                .setParameter(1,password)
//                .setParameter(2,id)
//                .executeUpdate();
//    }
//
//    @Override
//    public void verifyUser(Long id) {
//        em.createNativeQuery("UPDATE users SET verified = ? WHERE id=?")
//                .setParameter(1,true)
//                .setParameter(2,id)
//                .executeUpdate();
//    }
//
//    @Override
//    public void updateLocale(long id, String locale) {
//        em.createNativeQuery("UPDATE users SET locale = ? WHERE id=?")
//                .setParameter(1,locale)
//                .setParameter(2,id)
//                .executeUpdate();
//    }
}
