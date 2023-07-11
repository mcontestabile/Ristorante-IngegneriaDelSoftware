package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.users.registered_users.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe rappresentativa dell'addetto, il quale
 * permette l'aggiunta delle prenotazioni e il loro salvataggio sul relativo file.
 */
public class ReservationsAgent extends User {
    /**
     * Coperti attualmente raggiunti con le prenotazioni.
     */
    private int copertiRaggiunti = 0;
    /**
     * Carico di lavoro attualmente raggiunto con i menù/piatti delle prenotazioni.
     */
    private double caricoRaggiunto = 0.0;

    /**
     * La lista delle prenotazioni.
     */
    private List<ReservationItemList> reservations;

    /**
     * Repository archivio prenotazioni
     */
    private ReservationArchiveRepository reservationArchiveRepository;

    /**
     * La lista dei workloads di giornata.
     */
    private List<Workload> workloads;

    /**
     * Map per reperire i carichi di lavoro in maniera pronta e rapida.
     */
    private Map<String, Workload> workloadsMap;

    /**
     * Costruttore dell'oggetto addetto. Quando inizializzato, esso deve recuperare
     * le informazioni contenute nei file .xml di sua competenza e
     * configurare la repository per l'archivio delle prenotazioni.
     *
     * @param username username.
     * @param password password.
     * @param canIWork determina se l'addetto alle prenotazioni può lavorare o meno.
     */
    public ReservationsAgent(String username, String password, boolean canIWork) {
        super(username, password, canIWork);
        this.reservationArchiveRepository = new XMLReservationArchiveRepository();
        reservations = new ArrayList<>();
    }

    /**
     * Metodo che restituisce i coperti del ristorante attualmente raggiunti.
     *
     * @return coperti_raggiunti
     */
    public int getCopertiRaggiunti() {
        return copertiRaggiunti;
    }

    /**
     * Metodo che restituisce il carico di lavoro attualmente raggiunto.
     *
     * @return carico_raggiunto
     */
    public double getCaricoRaggiunto() {
        return caricoRaggiunto;
    }

    /**
     * Metodo che restituisce la lista di prenotazioni
     * caratterizzate da un nome, coperti occupati, lista di piatti/menu.
     *
     * @return reservations
     */
    public List<ReservationItemList> getReservations() {
        return reservations;
    }

    /**
     * Metodo che restituisce la lista dei carichi di lavoro
     * di ciascun piatto/menu disponibile.
     *
     * @return workloads
     */
    public List<Workload> getWorkloads() {
        return workloads;
    }

    /**
     * Metodo che restituisce la Map dei carichi di lavoro.
     * Map per reperire i carichi di lavoro in maniera pronta e rapida.
     *
     * @return workloadsMap
     */
    public Map<String, Workload> getWorkloadsMap() {
        return workloadsMap;
    }

    /**
     * Metodo che restituisce la repository utilizzata
     * per il salvataggio nell'archivio delle prenotazioni.
     *
     * @return reservationArchiveRepository
     */
    public ReservationArchiveRepository getReservationArchiveRepository() {
        return reservationArchiveRepository;
    }

    /**
     * Metodo per impostare il nuovo numero di coperti raggiunto.
     *
     * @param newCopertiRaggiunti, il nuovo numero di coperti da impostare
     */
    public void setCopertiRaggiunti(int newCopertiRaggiunti){this.copertiRaggiunti = newCopertiRaggiunti;}

    /**
     * Metodo per impostare il nuovo carico di lavoro raggiunto.
     * Utile ai controlli per evitare di eccedere il carico di lavoro totale del ristorante.
     *
     * @param newCaricoRaggiunto, il nuovo carico da impostare
     */
    public void setCaricoRaggiunto(double newCaricoRaggiunto) {
        this.caricoRaggiunto = newCaricoRaggiunto;
    }

    /**
     * Metodo per impostare i workloads ai quali l'agent si riferisce.
     * Vengono poi inseriti nella map, per una ricerca più veloce al bisogno.
     *
     * @param workloads la lista di workloads.
     */
    public void setWorkloads(List<Workload> workloads) {
        this.workloads = workloads;
        workloadsMap = new HashMap<>();
        workloads.forEach(w -> workloadsMap.put(w.getName(), w));
    }
}
