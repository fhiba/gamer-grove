package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.User;

import java.util.Optional;
import java.util.List;
public interface UserDao {
        Optional<User> findById(long id);

        Optional<User> findByEmail(final String mail);

        Optional<User> findByUsername(final String username);
        User create(final String username, final String email, final String password);

        List<User> findAll();

        void updateImageId(long id, long imageId);

        void updatePassword(Long id, String password);
        void verifyUser(Long id);

        void updateLocale(long id, String locale);
}
