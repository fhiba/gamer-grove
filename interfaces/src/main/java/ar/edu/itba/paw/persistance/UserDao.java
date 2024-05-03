package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.User;

import java.util.Optional;
import java.util.List;
public interface UserDao {
        Optional<User> findById(long id);

        Optional<User> findByEmail(final String mail);

        Optional<User> findByUsername(final String username);
        User create(final String username, final String email, final String password);

        Optional<Boolean> isAdmin(final long id);

        List<User> findByCommunity(String communityName);

        List<User> findAll();

        void updateImageId(long id, long imageId);
}
