package it.unibs.ingsw.users.reservations_agent;

/**
 * Classe che rappresenta un generico item del ristorante.
 * Avrà quindi un nome e un numero coperti.
 * Un item potrà essere ad esempio di tipo Menu o Piatto.
 */
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
}
