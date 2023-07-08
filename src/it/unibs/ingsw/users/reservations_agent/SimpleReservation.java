package it.unibs.ingsw.users.reservations_agent;

public class SimpleReservation implements Reservable{
    /**
     * Nome prenotazione
     */
     protected String name;
    /**
     * Numero coperti prenotazione.
     */
     protected int resCover;

    /**
     * Costruttore, accetta i parametri caratterizzanti di una prenotazione
     * ancora in uno stato basico, primordiale
     *
     * @param name nome prenotazione.
     * @param resCover numero coperti prenotazione.
     *
     */
    public SimpleReservation(String name, int resCover) {
        this.name = name;
        this.resCover = resCover;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getResCover() {
        return resCover;
    }

}
