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
import java.util.HashSet;
import java.util.Set;

@Component
public class PawUserDetailsService implements UserDetailsService {


    @Autowired
    private UserService us;

    @Autowired
    public PawUserDetailsService(final UserService us){
        this.us = us;
    }
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = us.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User "+ username +" not found"));
        final Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(user.isVerified()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VERIFIED"));
        }
        if(user.getOwner()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new PawAuthUser(user.getUsername(), user.getPassword(), authorities);
    }
}
