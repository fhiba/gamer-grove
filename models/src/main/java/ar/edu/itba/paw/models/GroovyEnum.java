package ar.edu.itba.paw.models;

public enum GroovyEnum {
    UP(1),
    DOWN(-1),
    UP_FROM_DOWN(2),
    DOWN_FROM_UP(-2);

    private final int value;
    GroovyEnum(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public static GroovyEnum fromValue(int value) {
        for (GroovyEnum e : GroovyEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }

    public static Boolean isValidInput(int value) {
        return value == UP.value || value == DOWN.value;
    }

    public static GroovyEnum fromBoolean(Boolean value) {
        if (value) {
            return UP;
        } else {
            return DOWN;
        }
    }
}
