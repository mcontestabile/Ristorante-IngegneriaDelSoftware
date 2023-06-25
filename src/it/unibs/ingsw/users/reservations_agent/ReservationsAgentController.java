package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;

public class ReservationsAgentController extends UserController {
    private ReservationsAgent agent;

    /**
     * Costruttore del controller.
     * @param userQueue la lista degli utenti con cui interagisce.
     * @param agent l'utente specifico di questo controller.
     */
    public ReservationsAgentController(Queue<User> userQueue, ReservationsAgent agent) {
        super(userQueue);
        this.agent = agent;
    }

    public double getCaricoRaggiunto() {return agent.getCaricoRaggiunto();}

    public void parseCourses(){
        try {
            agent.setMenu(agent.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    public void parseWorkloads(){
        try {
            agent.setWorkloads(agent.parsingTask(UsefulStrings.WORKLOADS_FILE, Workload.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo che inserisce una Reservation e la scrive nell'agenda.
     *
     * @param name nome prenotazione.
     * @param resCover coperti prenotazione.
     * @param itemList lista di item (menù/piatti) di una prenotazione.
     */
    public void insertReservation(String name, String resCover, HashMap<String, String> itemList) {

        Reservable r = new SimpleReservation(name, resCover);

        r = new ReservationItemList(r, itemList);

        agent.getReservations().add((ReservationItemList) r);

        try {
            agendaWritingTask(agent.getReservations());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che scrive effettivamente su file quanto presente nell'elenco delle prenotazioni.
     *
     * @param reservations l'elenco delle prenotazioni.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void agendaWritingTask(ArrayList<ReservationItemList> reservations) throws XMLStreamException{
        XMLWriter writer = new XMLWriter(UsefulStrings.AGENDA_FILE);
        writer.writeArrayListXML(reservations, UsefulStrings.AGENDA_OUTER_TAG);
    }

    /**
     * Metodo che fornisce l'elenco dei nomi presenti nell'elenco prenotazioni dell'addetto.
     * Utile in fase di controllo per evitare l'inserimento di nomi duplicati.
     *
     * @return names
     */
    public ArrayList<String> getReservationNameList(){
        ArrayList<String> names = new ArrayList<>();

        for(Reservable r : agent.getReservations()){
            names.add(r.getName());
        }
        return names;
    }

    /**
     * Metodo che chiede all'utente il nome di una prenotazione.
     * Il valore non è valido, se è già presente nella lista delle prenotazioni.
     *
     * @return name, nome della prenotazione.
     */
    public String askResName(){
        String name;

        do{
            name = DataInput.readNotEmptyString(UsefulStrings.RESERVATION_NAME);
        }while(isRepeated(name, this.getReservationNameList()));

        return name;
    }

    /**
     * Metodo che controlla se un nome, dato in input, è già presente nella
     * collezione data in input.
     *
     * @param s nome sul quale effettuare il controllo.
     * @param names collezione generica di nomi.
     */
    public boolean isRepeated(String s, Collection<String> names){
        for (String x : names){
            if(x.equals(s)){
                System.out.println(UsefulStrings.ELEMENT_ALREADY_IN);
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo che chiede all'utente il nome di un menù/piatto valido.
     * Non è valido se non è presente nel menù disponibile nella giornata lavorativa relativa,
     * oppure se è un nome ripetuto, oppure se il workload dell'item (esteso ad una singola persona) eccede il carico sostenibile del ristorante.
     *
     * @param item_list la lista di items della prenotazione.
     * @param restaurantWorkload carico di lavoro sostenubile del ristorante.
     * @return menu_piatto valido
     */
    public String askMenuPiatto(HashMap<String, String> item_list, double restaurantWorkload){
        String menu_piatto;
        do{
            menu_piatto = DataInput.readNotEmptyString(UsefulStrings.MENU_DISH_NAME);
        }while(!this.isInMenu(menu_piatto) ||
                this.isRepeated(menu_piatto, item_list.keySet()) ||
                this.exceedsRestaurantWorkload(this.calculateWorkload(menu_piatto, 1), this.getCaricoRaggiunto(), restaurantWorkload));


        return menu_piatto;
    }

    /**
     * Metodo per controllare se l'item in input sia effetteivamente disponibile
     * Se si tratta di un menu ritorna true se vi è esito positivo. Altrimenti si cerca nei piatti relativi
     * e se vi è esito positivo ritorna true anche in questo caso, testimoniando la
     * disponibilità dell'item in input. Se l'item non si trova in nessuno dei due casi, allora il metodo ritorna false.
     *
     * @param menu_piatto nome del menù/piatto da verificare.
     */
    public boolean isInMenu(String menu_piatto) {
        for (Course c : agent.getMenu()) {
            if(menu_piatto.equals(c.getName()))
                return true;
            for(String d : c.getDishesArraylist()){
                if(menu_piatto.equals(d))
                    return true;
            }
        }
        System.out.println(UsefulStrings.INVALID_MENU_DISH);
        return false;
    }

    /**
     * Metodo che controlla se i coperti raggiunti, estesi al parametro resCover in input, superi
     * la capienza totale del ristorante.
     *
     * @param resCover parametro da sommare alla totalità dei coperti raggiunti.
     * @param coperti_raggiunti coperti attualmente raggiunti.
     * @param cover_totali capienza massima del ristorante.
     */
    public boolean exceedsCover(int resCover, int coperti_raggiunti, int cover_totali){
        int partialSum = coperti_raggiunti + resCover;
        int postiDispondibili = cover_totali - coperti_raggiunti;

        if(partialSum > cover_totali){
            System.out.println(UsefulStrings.COVER_EXCEEDED_AVAILABLE + postiDispondibili + "\n\n");
            return true;
        }

        return false;
    }

    /**
     * Metodo che chiede all'utente il numero coperti di una prenotazione.
     * Il valore non è valido se la totalità dei coperti raggiunti, estesa al valore
     * inserito dall'utente, supera la capienza massima del ristorante.
     *
     * @param totCover capienza massima del ristorante.
     * @return resCover, i nuovi coperi attialmente raggiunti.
     */
    public int askResCover(int totCover){
        int resCover;
        do{
            resCover = DataInput.readPositiveInt(UsefulStrings.RES_COVER);
        }while(exceedsCover(resCover, agent.getCopertiRaggiunti(), totCover));

        return resCover;
    }

    /**
     * Metodo che controlla se la totalità dei menù tematici estesa all'itemCover in input,
     * superi il numero coperti della prenotazione, in modo tale da assicurare che si possa
     * scegliere alpiù un menù a testa in fase di prenotazione.
     *
     * @param itemCover numero coperti per l'item (menù/piatto).
     * @param nMenuAlready totalità dei menù già presenti nella prenotazione.
     * @param resCover capienza massima ristorante.
     */
    public boolean exceedsOneMenuPerPerson(int itemCover, int nMenuAlready, int resCover){
        int partial = nMenuAlready + itemCover;

        if(partial > resCover){
            System.out.println("Ci sono " + partial + " menù tematici per " + resCover + " persone!");
            return true;
        }

        return false;
    }

    /**
     * Metodo che controlla se il carico di lavoro raggiunto, esteso al parametro workloadSum in input, superi
     * il carico di lavoro sostenibile dal ristorante.
     *
     * @param workloadSum parametro da sommare alla totalità di workload raggiunta.
     * @param workload_raggiunti carico di lavoro attualmente raggiunto.
     * @param resturantWorkload carico di lavoro sostenibile del ristorante.
     */
    public boolean exceedsRestaurantWorkload(double workloadSum, double workload_raggiunti, double resturantWorkload){
        double partialSum = workload_raggiunti + workloadSum;
        double workloadRestante = resturantWorkload - workload_raggiunti;

        if(partialSum > resturantWorkload){
            System.out.println(UsefulStrings.WORKLOAD_EXCEEDED_AVAILABLE + workloadRestante + "\n\n");
            return true;
        }

        return false;
    }

    /**
     * Metodo che controlla se il carico di lavoro sostenibile del ristorante non è stato superato.
     *
     * @param restaurantWorkload il carico sostenibile del ristorante.
     */
    public boolean workloadRestaurantNotExceeded(double restaurantWorkload){
        double workloadRimanente = restaurantWorkload - this.getCaricoRaggiunto();
        if(this.getCaricoRaggiunto() >= restaurantWorkload || workloadRimanente < getMinimumWorkload()){
            System.out.println(UsefulStrings.NO_MORE_RES_WORKLOAD);
            return false;
        }
        return true;
    }

    /**
     * Metodo che calcola il workload dell'item (menù/piatto) passato come parametro, esteso al numero dei coperti che l'hanno selezionato in fase di prenotazione.
     *
     * @param i item (menù/piatto) del quale si vuole sapere il workload.
     * @param cover numero coperti per l'item (menù/piatto) in questione.
     * @return workload dell'item (menù/piatto) esteso al numero dei coperti che l'hanno selezionato in fase di prenotazione.
     */
    public double calculateWorkload(String i, int cover){
        Fraction w = agent.getWorkloadsMap().get(i).getWorkloadFraction();
        return w.getTwoDecimalNumber()*cover;
    }

    /**
     * Metodo che ritorna il workload minimo della giornata.
     * Utile in fase di controllo, in quanto se il ristorante raggiunge un workload minore del valore
     * ritornato dal metodo, non si potranno più prendere prenotazioni, in quanto qualsiasi piatto
     * farà superare il carico di lavoro sostenibile dal ristorante.
     *
     * @return valore dell'workload minimo (a due cifre decimali).
     */
    public double getMinimumWorkload(){
        ArrayList<Workload> workloads = agent.getWorkloads();

        Fraction min = workloads.get(0).getWorkloadFraction();
        double dMin = min.getTwoDecimalNumber();

        for(int i=0; i<workloads.size(); i++){
            if(workloads.get(i).getWorkloadFraction().getTwoDecimalNumber() < dMin) {
                min = workloads.get(i).getWorkloadFraction();
                dMin = min.getTwoDecimalNumber();
            }
        }
        return dMin;
    }

    /**
     * Metodo che controlla se l'item in input sia un piatto, e non un menu tematico.
     * Se l'item inserito avrà una corrispondenza nell'elenco dei menu tematici, allora si tratterà
     * di un menu, perciò si restituirà false. Altrimenti si tratterà di un piatto normale, quindi
     * si restituirà true.
     *
     * @param menu_piatto nome del menù/piatto da verificare.
     */
    public boolean isDish(String menu_piatto) {
        for (Course c : agent.getMenu()) {
            if(menu_piatto.equals(c.getName()))
                return false;
        }

        return true;
    }
}
