package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.models.User;

import java.util.Optional;
import java.util.List;
public interface UserService {

    Optional<User> findById(final long id);
    Optional<User> findByEmail(final String email);

    Optional<User> findByUsername(final String username);
    User create(String username, final String email, final String password);

    Optional<User> getLoggedUser();

    Boolean isUserAdmin(final long id);

    User getLoggedUserChecked() throws NoLoggedUserException;

    List<User> findAll();
}
