package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private static final String BEARER = "Bearer";
    private static final String AUTH_HEADER = "X-GamerGrove-AuthToken";
    private static final String REFRESH_HEADER = "X-GamerGrove-RefreshToken";
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Autowired
    private UserService us;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        LOGGER.info("Header: {}", header);
        if (header == null || !header.startsWith(BEARER + " ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();
        final JwtDetails jwtDetails = jwtUtil.validateToken(token);

        if (jwtDetails == null) {
            LOGGER.info("Invalid JWT token");

            response.addHeader("WWW-Authenticate", "Bearer realm=\"GamerGrove\"");
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtDetails.getUsername());

        if (userDetails == null || !userDetails.isEnabled()
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            LOGGER.info("Invalid user details isEnabled:{}", userDetails != null ? userDetails.isEnabled() : "null");

            response.addHeader("WWW-Authenticate", "Bearer realm=\"GamerGrove\"");
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtDetails.getTokenType() == JwtType.REFRESH) {
            final Optional<User> maybeUser = us.findByUsername(jwtDetails.getUsername());
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();
                response.setHeader(REFRESH_HEADER, jwtUtil.createTokenHeader(user, JwtType.REFRESH));
                response.setHeader(AUTH_HEADER, jwtUtil.createTokenHeader(user, JwtType.AUTH));
            }
        }

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(

                userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        LOGGER.info("User {} authenticated, setting security context, with authorities: {}", userDetails.getUsername(),
                userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
