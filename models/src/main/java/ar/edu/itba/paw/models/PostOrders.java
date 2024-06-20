package ar.edu.itba.paw.models;

public enum PostOrders {
    NEWEST("newest"),
    OLDEST("oldest"),
    HOTTEST("hottest"),
    DEFAULT("default");

    private final String order;

    PostOrders(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }

    public static PostOrders fromString(String order) {
        for (PostOrders o : PostOrders.values()) {
            if (o.order.equalsIgnoreCase(order)) {
                return o;
            }
        }
        return DEFAULT;
    }

}
