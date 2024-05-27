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
        return this.category;
    }

    public static CommunityCategories fromString(String category) {
        return switch (category) {
            case "Action" -> CommunityCategories.Action;
            case "Adventure" -> CommunityCategories.Adventure;
            case "Cards" -> CommunityCategories.Cards;
            case "Strategy" -> CommunityCategories.Strategy;
            case "Sports" -> CommunityCategories.Sports;
            case "Simulation" -> CommunityCategories.Simulation;
            case "RPG" -> CommunityCategories.RPG;
            case "Puzzle" -> CommunityCategories.Puzzle;
            case "Platformer" -> CommunityCategories.Platformer;
            case "Shooter" -> CommunityCategories.Shooter;
            case "Hack and Slash" -> CommunityCategories.Hack_and_Slash;
            case "PvP" -> CommunityCategories.PvP;
            case "PvE" -> CommunityCategories.PvE;
            case "Open World" -> CommunityCategories.Open_World;
            default -> throw new IllegalArgumentException("Unknown value: " + category);
        };
    }

}
