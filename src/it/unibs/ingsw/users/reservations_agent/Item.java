package it.unibs.ingsw.users.reservations_agent;

public abstract class Item {
    String name;
    int cover;

    public Item(String name, int cover) {
        this.name = name;
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }
}
