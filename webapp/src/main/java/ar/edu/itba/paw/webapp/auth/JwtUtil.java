package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.util.Date;

import ar.edu.itba.paw.models.User;

@Component
public class JwtUtil {
    private static final int LIFE_TIME = 7 * 24 * 60 * 60 * 1000; // 1 week (in millis)
    @Autowired
    private UserDetailsService userDetailsService;

    private final Key jwtKey;

    public JwtUtil(Resource jwtKeyResource) throws IOException {
        this.jwtKey = Keys.hmacShaKeyFor(
                FileCopyUtils.copyToString(new InputStreamReader(jwtKeyResource.getInputStream()))
                        .getBytes(StandardCharsets.UTF_8));
    }

    public UserDetails parseToken(String jwt) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            if (new Date(System.currentTimeMillis()).after(claims.getExpiration())) {
                return null;
            }

            final String username = claims.getSubject();

            return userDetailsService.loadUserByUsername(username);

        } catch (Exception e) {
            return null;
        }
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims();

        claims.setSubject(user.getUsername());
        claims.put("ownership", user.getOwner());
        claims.put("mod", user.getModderCommunities());
        return "Bearer " + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + LIFE_TIME))
                .signWith(jwtKey)
                .compact();

    }

}
