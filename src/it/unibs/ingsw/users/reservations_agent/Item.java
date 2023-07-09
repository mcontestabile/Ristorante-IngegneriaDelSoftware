package it.unibs.ingsw.users.reservations_agent;

public abstract class Item {
    String name;
    int resCover;

    public Item(String name, int resCover) {
        this.name = name;
        this.resCover = resCover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResCover() {
        return resCover;
    }

    public void setResCover(int resCover) {
        this.resCover = resCover;
    }
}
