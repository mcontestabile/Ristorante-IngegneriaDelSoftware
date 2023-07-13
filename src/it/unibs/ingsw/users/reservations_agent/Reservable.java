package it.unibs.ingsw.users.reservations_agent;

/**
 * Intreffaccia per la creazione di prenotazioni basic.
 * Una prenotazione semplice avrà quindi un nome e un numero coperti,
 * e dovrà implementare i metodi per restituire suddetti valori.
 */
public interface Reservable {
    String getName();
    int getResCover();
}
