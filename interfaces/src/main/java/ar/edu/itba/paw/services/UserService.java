package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(final long id);
    Optional<User> findByEmail(final String email);

    Optional<User> findByUsername(final String username);
    User create(String username, final String email, final String password);
}
