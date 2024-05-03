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
}
