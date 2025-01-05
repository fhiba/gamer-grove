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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ar.edu.itba.paw.services.UserService;

import org.springframework.security.core.Authentication;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    private static final int USER = 0;
    private static final int PASSWORD = 1;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService us;

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String[] credentials = extractAndDecodeCredentials(header);
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(credentials[USER], credentials[PASSWORD]));
            us.findByUsername(credentials[USER]).ifPresent(user -> {
                response.setHeader(HttpHeaders.AUTHORIZATION, jwtUtil.createToken(user));
            });
            
        } catch (Exception e) {
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
