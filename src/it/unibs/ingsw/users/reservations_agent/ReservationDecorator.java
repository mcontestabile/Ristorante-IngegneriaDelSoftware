package it.unibs.ingsw.users.reservations_agent;

public abstract class ReservationDecorator implements Reservable{
    protected final Reservable decoratedReservation;

    public ReservationDecorator(Reservable decoratedReservation) {
        this.decoratedReservation = decoratedReservation;
    }

    public String getReservation(){
        return decoratedReservation.getReservation();
    }
}
