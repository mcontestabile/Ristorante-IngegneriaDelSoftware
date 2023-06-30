package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.utilities.RestaurantDates;
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

    //public double getOverallWorkload() {return agent.getRestaurantWorkload();}

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
     * Metodo che controlla se il ristorante non è pieno.
     *
     * @param totCover capienza massima del ristorante.
     */
    public boolean restaurantNotFull(int totCover){
        if(this.getCopertiRaggiunti() >= totCover){
            System.out.println(UsefulStrings.NO_MORE_RES_COVER);
            return false;
        }
        return true;
    }

    /**
     * Metodo che aggiunge un item (menù/piatto) nella lista di items di una prenotazione.
     * Aggiungerà l'elemento alla lista e restituirà quindi la nuova quantità selezionata per il tipo dell'item (menù/piatto) in questione.
     * Sarà la somma dei piatti normali, o la somma dei menù, presi durante una prenotazione.
     *
     * @param itemCover numero coperti per l'item da aggiungere.
     * @param sumGenericItemCover somma coperti attuale dell'item (può essere la somma dei coperti presi per un piatto, o per un menù)
     * @param menu_piatto nome dell'item da aggiungere.
     * @param item_list lista di item della prenotazione.
     * @return sumGenericItemCover, la nuova somma coperti attuale dell'item, dopo l'inserimento nella lista.
     */
    public int addItem(int itemCover, int sumGenericItemCover, String menu_piatto, HashMap<String, String> item_list){
        sumGenericItemCover += itemCover;

        String item_cover = Integer.toString(itemCover);
        item_list.put(menu_piatto, item_cover);

        return sumGenericItemCover;
    }

    /**
     * Metodo che in fase di inserimento di una prenotazione, verifica se per
     * completare quest'ultima ci sia il bisogno di inserire altri items per adempire
     * al numero coperti della prenotazione stessa; in quanto si devono avere almeno n items se la prenotazione
     * è di n coperti.
     *
     * @param nItems numero di piatti attuali nella lista
     * @param nMenu numero di menù attuali nella lista
     * @param resCover numero coperti della prenotazione.
     */
    public boolean moreItemsNeeded(int nItems, int nMenu, int resCover){
        int sumItemsCover = nItems + nMenu;

        if(sumItemsCover < resCover && nMenu <= resCover)
            return true;

        return false;
    }

    /**
     * Metodo che ritorna il menù disponibile.
     *
     * @return menu
     */
    public ArrayList<Course> getMenu(){return agent.getMenu();}

    /**
     * Metodo che restituisce i coperti del ristorante attualmente raggiunti.
     *
     * @return coperti_raggiunti
     */
    public int getCopertiRaggiunti() {
        return agent.getCopertiRaggiunti();
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
     * Metodo che aggiorna i coperti raggiunti del ristorante.
     * Si somma quindi ai coperti attuali, un nuovo valore, aumentando così di quell'ammontare, il numero di persone attualmente prenotate presso il ristorante.
     *
     * @param nuoviCoperti coperti da sommare agli attuali.
     */
    public void updateCopertiRaggiunti(int nuoviCoperti) {
        int copertiRaggiunti = agent.getCopertiRaggiunti();
        agent.setCovered(copertiRaggiunti + nuoviCoperti);
    }

    /**
     * Metodo che aggiorna il carico di lavoro del ristorante raggiunto.
     * Si somma quindi al carico attuale, un nuovo valore, aumentando così di quell'ammontare, il totale del carico di lavoro attualmente sostenuto dal ristorante.
     * Si effettua poi una trasformazione, richiamando la libreria Math, in modo tale da avere 2 cifre decimali.
     *
     * @param nuovoCarico carico da sommare all'attuale.
     */
    public void updateCaricoRaggiunto(double nuovoCarico) {
        double newCaricoRaggiunto = Math.floor((agent.getCaricoRaggiunto() + nuovoCarico) * 100)/100;
        agent.setCaricoRaggiunto(newCaricoRaggiunto);
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

    public void saveInReservationArchive(){
        agent.getReservationArchiveRepository().save(RestaurantDates.workingDay.format(RestaurantDates.formatter));
    }
}
