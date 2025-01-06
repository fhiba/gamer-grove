package ar.edu.itba.paw.webapp.auth;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
    private static final int AUTH_TOKEN_LIFE_TIME = 15 * 60 * 1000; // 15 minutes (in millis)
    private static final int REFRESH_TOKEN_LIFE_TIME = 7 * 24 * 60 * 60 * 1000; // 1 week (in millis)
    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLE = "role";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_VERIFIED = "ROLE_VERIFIED";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    private UserDetailsService userDetailsService;

    private final Key jwtKey;

    public JwtUtil(Resource jwtKeyResource) throws IOException {
        this.jwtKey = Keys.hmacShaKeyFor(
                FileCopyUtils.copyToString(new InputStreamReader(jwtKeyResource.getInputStream()))
                        .getBytes(StandardCharsets.UTF_8));
    }

    public String createTokenHeader(User user, JwtType type) {
        Claims claims = Jwts.claims();

        long lifeTime;
        if (type == JwtType.REFRESH) {
            lifeTime = REFRESH_TOKEN_LIFE_TIME;
            claims.put(CLAIM_TYPE, JwtType.REFRESH.toString());

        } else {
            lifeTime = AUTH_TOKEN_LIFE_TIME;
            claims.put(CLAIM_TYPE, JwtType.AUTH.toString());
            claims.put(CLAIM_ROLE, user.getOwner() ? ROLE_ADMIN : (user.isVerified() ? ROLE_VERIFIED : ROLE_USER));

        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + lifeTime))
                .signWith(jwtKey)
                .compact();

    }

    public JwtDetails validateToken(String token) {
        try {
            final Jws<Claims> parsedClaims = Jwts.parserBuilder()
                    .setSigningKey(jwtKey)
                    .build()
                    .parseClaimsJws(token);
            final Claims claims = parsedClaims.getBody();
            if (claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
                return null;
            }
            return new JwtDetails.Builder()
                    .token(token)
                    .username(claims.getSubject())
                    .issuedDate(claims.getIssuedAt())
                    .expirationDate(claims.getExpiration())
                    .tokenType(JwtType.fromString(claims.get(CLAIM_TYPE, String.class)))
                    .build();

        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
}
