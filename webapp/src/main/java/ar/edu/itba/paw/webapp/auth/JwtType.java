package ar.edu.itba.paw.webapp.auth;

public enum JwtType {
    AUTH("auth"), REFRESH("refresh");

    private final String type;

    private JwtType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static JwtType fromString(String type) {
        for (JwtType t : JwtType.values()) {
            if (t.type.equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }
}
