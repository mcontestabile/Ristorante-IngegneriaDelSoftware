package it.unibs.ingsw.users.reservations_agent;

/**
 * Classe decorator.
 */
public class ReservationDecorator implements Reservable{
    protected final Reservable decoratedReservation;

    public ReservationDecorator(Reservable decoratedReservation) {
        this.decoratedReservation = decoratedReservation;
    }

    @Override
    public String getName() {
        return decoratedReservation.getName();
    }

    @Override
    public int getResCover() {
        return decoratedReservation.getResCover();
    }
}
