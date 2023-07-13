package it.unibs.ingsw.users.reservations_agent;

/**
 * Interfaccia per il salvataggio nell'archivio delle prenotazioni.
 * Caratterizzata dal metodo save con il giorno lavorativo in formato
 * stringa in ingresso.
 */
public interface ReservationArchiveRepository {
    void save(String workingday);
}
