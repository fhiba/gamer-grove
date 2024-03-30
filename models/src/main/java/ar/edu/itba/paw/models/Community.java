package ar.edu.itba.paw.models;

public class Community {

    private final long id;
    private final String name;
    //private final String description;
    //private final long owner_id;
    private final long portrait_id;

    public Community(final long id, final String name, final long portrait_id) {
        this.id = id;
        this.name = name;
        //this.description = description;
        //this.owner_id = owner_id;
        this.portrait_id = portrait_id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPortrait_id() {
        return portrait_id;
    }
}
