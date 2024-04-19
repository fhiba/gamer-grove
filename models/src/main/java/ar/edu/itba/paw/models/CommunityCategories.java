package ar.edu.itba.paw.models;

public enum CommunityCategories {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    CARDS("Cards"),
    STRATEGY("Strategy"),
    SPORTS("Sports"),
    SIMULATION("Simulation"),
    RPG("RPG"),
    PUZZLE("Puzzle"),
    PLATFORMER("Platformer"),
    SHOOTER("Shooter"),
    HACK_AND_SLASH("Hack and Slash"),
    PVP("PvP"),
    PVE("PvE"),
    OPEN_WORLD("Open World");

    private final String category;

    CommunityCategories(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
