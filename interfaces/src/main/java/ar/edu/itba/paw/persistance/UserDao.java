package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

import java.util.Optional;
import java.util.List;
public interface UserDao {
        Optional<User> findById(long id);

        Optional<User> findByEmail(final String mail);

        Optional<User> findByUsername(final String username);
        User create(final String username, final String email, final String password);

        List<User> findAll();

        User updatePassword(User user, String password);
        User verifyUser(User user);

        User updateLocale(User user, String locale);

        User updateImage(User user, File image);

        List<User> getFollowersOfCommunity(long communityId);
}
