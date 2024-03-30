package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ar.edu.itba.paw.models.User;

import java.util.Arrays;
import java.util.Collection;

@Component
public class PawUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final User user = us.findByUsername(s).orElseThrow(() -> new UsernameNotFoundException("User "+ s +" not found"));
        final Collection<? extends GrantedAuthority> authorities = Arrays.asList( new SimpleGrantedAuthority("ROLE_USER"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
