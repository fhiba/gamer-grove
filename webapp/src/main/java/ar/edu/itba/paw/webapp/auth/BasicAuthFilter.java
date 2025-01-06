package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ar.edu.itba.paw.services.TokenService;
import ar.edu.itba.paw.services.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    private static final int USER = 0;
    private static final int PASSWORD = 1;
    private static final String BASIC = "Basic";
    private static final String AUTH_HEADER = "X-GamerGrove-AuthToken";
    private static final String REFRESH_HEADER = "X-GamerGrove-RefreshToken";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService us;

    @Autowired
    private TokenService ts;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);

    @Autowired
    private PawUserDetailsService pawUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BASIC + " ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String[] credentials = extractAndDecodeCredentials(header);
            // asume formato Basic base64(user:token)
            if (ts.verifyVerifyToken(credentials[PASSWORD])) {
                LOGGER.debug("Token {}, is a verify token", credentials[PASSWORD]);
                us.verifyUser(credentials[PASSWORD]);
                final UserDetails userDetails = pawUserDetailsService.loadUserByUsername(credentials[USER]);
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {

                final Authentication authentication = authenticationManager
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(credentials[USER], credentials[PASSWORD]));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            us.findByUsername(credentials[USER]).ifPresent(user -> {
                response.setHeader(REFRESH_HEADER, jwtUtil.createTokenHeader(user, JwtType.REFRESH));
                response.setHeader(AUTH_HEADER, jwtUtil.createTokenHeader(user, JwtType.AUTH));
            });

        } catch (

        Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid credentials"));
            return;

        }
    }

    private String[] extractAndDecodeCredentials(String header) {
        byte[] base64Token = header.split(" ")[1].trim().getBytes(StandardCharsets.UTF_8);
        byte[] decoded;

        try {
            decoded = Base64.decode(base64Token);

        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        if (!token.contains(":")) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }

        return token.split(":", 2);
    }

}
