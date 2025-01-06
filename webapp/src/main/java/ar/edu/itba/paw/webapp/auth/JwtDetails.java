package ar.edu.itba.paw.webapp.auth;

import java.util.Date;

public class JwtDetails {
    private final String token;
    private final String username;
    private final Date issuedDate;
    private final Date expirationDate;
    private final JwtType tokenType;

    public JwtDetails(String token, String username, Date issuedDate, Date expirationDate, JwtType tokenType) {
        this.token = token;
        this.username = username;
        this.issuedDate = issuedDate;
        this.expirationDate = expirationDate;
        this.tokenType = tokenType;
    }

    public static class Builder {
        private String token;
        private String username;
        private Date issuedDate;
        private Date expirationDate;
        private JwtType tokenType;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder issuedDate(Date issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder expirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder tokenType(JwtType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public JwtDetails build() {
            return new JwtDetails(token, username, issuedDate, expirationDate, tokenType);
        }
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public JwtType getTokenType() {
        return tokenType;
    }
}
