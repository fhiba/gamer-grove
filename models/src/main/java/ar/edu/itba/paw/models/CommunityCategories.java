package ar.edu.itba.paw.models;

public enum CommunityCategories {
    Action("Action"),
    Adventure("Adventure"),
    Cards("Cards"),
    Strategy("Strategy"),
    Sports("Sports"),
    Simulation("Simulation"),
    RPG("RPG"),
    Puzzle("Puzzle"),
    Platformer("Platformer"),
    Shooter("Shooter"),
    Hack_and_Slash("Hack and Slash"),
    PvP("PvP"),
    PvE("PvE"),
    Open_World("Open World");

    private final String category;

    CommunityCategories(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

}
