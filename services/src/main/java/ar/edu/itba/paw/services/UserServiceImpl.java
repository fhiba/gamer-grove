package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NoLoggedUserException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistance.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User create(final String username, final String email, final String password) {

        return userDao.create(username, email, passwordEncoder.encode(password));
    }

    @Override
    public Optional<User> getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof org.springframework.security.core.userdetails.User ?
                findByUsername(((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                : Optional.empty();
    }

    @Override
    public Boolean isUserAdmin(long id) {
        return userDao.isAdmin(id).orElse(false);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User getLoggedUserChecked() throws NoLoggedUserException {
        Optional<User> maybeUser = getLoggedUser();
        if(maybeUser.isEmpty())
            throw new NoLoggedUserException("User not logged");
        return maybeUser.get();
    }

}
