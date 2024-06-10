package ar.edu.itba.paw.models;

public enum PostCategories {
    DISC("Discussion"),
    NEWS("News"),
    REVIEW("Review"),
    HELP("Help"),
    GUIDE("Guide"),
    RECOMMENDATION("Recommendation");
    private final String category;

    PostCategories(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public static PostCategories fromString(String category) {
        for (PostCategories c : PostCategories.values()) {
            if (c.category.equalsIgnoreCase(category)) {
                return c;
            }
        }
        return null;
    }
}
