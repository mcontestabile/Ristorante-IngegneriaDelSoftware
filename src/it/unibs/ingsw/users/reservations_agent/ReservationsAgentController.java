package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.utilities.RestaurantDates;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

import javax.xml.stream.XMLStreamException;
import java.util.*;

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

    /**
     * Metodo che effettua il parsing del file XML necessario
     * per configurare il menu disponibile;
     * il menu servirà al ReservationAgent in fase di prenotazione.
     */
    public void parseCourses(){
        try {
            agent.setMenu(agent.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo che effettua il parsing del file XML necessario
     * per configurare il carico di lavoro di ciascun menu/piatto disponibile;
     *  il carico di lavoro di ciascun menu/piatto disponibile servirà al ReservationAgent in fase di prenotazione.
     */
    public void parseWorkloads(){
        try {
            agent.setWorkloads(agent.parsingTask(UsefulStrings.WORKLOADS_FILE, Workload.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo che controlla se il ristorante non è pieno.
     */
    public boolean restaurantNotFull(){
        if(getCopertiRaggiunti() >= (int)getCovered()){
            System.out.println(UsefulStrings.NO_MORE_RES_COVER);
            return false;
        }
        return true;
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

        if((sumItemsCover < resCover) && (nMenu <= resCover))
            return true;

        return false;
    }

    /**
     * Metodo che ritorna il carico di lavoro raggiunto dal ristorante,
     *
     * @return caricoRaggiunto
     */
    public double getCaricoRaggiunto() {return agent.getCaricoRaggiunto();}

    /**
     * Metodo che ritorna il menù disponibile.
     *
     * @return menu
     */
    public ArrayList<Course> getMenu(){return (ArrayList<Course>) agent.getMenu();}

    /**
     * Metodo che restituisce i coperti del ristorante attualmente raggiunti.
     *
     * @return coperti_raggiunti
     */
    public int getCopertiRaggiunti() {
        return agent.getCopertiRaggiunti();
    }

    /**
     * SimpleReservation è una prenotazione caratterizzata solamente da un nome
     * e da un numero coperti. E' la base di una prenotazione.
     *
     * @param name, nome della prenotazione
     * @param resCover, coperti occupati
     * @return nuova prenotazione base
     */
    public SimpleReservation createSimpleReservation(String name, int resCover) {
        return new SimpleReservation(name, resCover);
    }

    /**
     * ReservationItemList è una prenotazione "completa". Oltre ad avere le caratteristiche
     * della SimpleReservation, sarà caratterizzata anche da un insieme di item (menu/piatto) che
     * caratterrizzano la prenotazione in questione (difatti questa verrà scritta nell'agenda).
     * I due concetti sono stati divisi in vista di un ipotetico cambiamento a livello dell'insieme di item,
     * che potrà comportare diverse scelte gestionali, non andando però ad intaccare la SimpleReservation,
     * che a meno di casi eccezionali dovrebbe rimanere pressoché stabile.
     *
     * @param decoratedRes, prenotazione base alla quale verrà aggiunta la lista di menu/piatti
     * @param itemList, la lista di menu/piatti
     * @return nuova prenotazione completa
     */
    public ReservationItemList createReservationItemList(Reservable decoratedRes, ItemList itemList){
        return new ReservationItemList(decoratedRes, itemList);
    }
    /**
     * Metodo che inserisce una Reservatio nell'insieme delle prenotazioni gestito dall'addetto.
     *
     * @param r, una prenotazione completa pronta per essere
     *           aggiunta all'insieme delle prenotazioni gestito dall'addetto
     */
    public void insertReservation(Reservable r) {
        agent.getReservations().add((ReservationItemList) r);
    }

    /**
     * Metodo che scrive effettivamente su file quanto presente nell'elenco delle prenotazioni.
     */
    public void writeAgenda() {
        try {
            agent.writingTask((agent.getReservations()), UsefulStrings.AGENDA_FILE, UsefulStrings.AGENDA_OUTER_TAG);
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
        agent.setCopertiRaggiunti((copertiRaggiunti + nuoviCoperti));
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
    public boolean isAlreadyIn(String s, Collection<String> names){
        for (String x : names){
            if(x.equals(s)){
                System.out.println(UsefulStrings.ELEMENT_ALREADY_IN);
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo per controllare se l'item in input sia effetteivamente disponibile.

     * @param menu_piatto nome del menù/piatto da verificare.
     */
    public boolean isInMenu(String menu_piatto) {
        // il menu del giorno non è considerato parte del menu
        if(isDailyMenu(menu_piatto))
            return false;

        if(isMenu(menu_piatto) || isDish(menu_piatto))
            return true;

        System.out.println(UsefulStrings.INVALID_MENU_DISH);
        return false;
    }

    private boolean isDish(String menu_piatto) {
        for (Course c : getMenu()) {
            for (String dish : c.getDishesArraylist()) {
                if (menu_piatto.equals(dish))
                    return true;
            }
        }

        return false;
    }


    private boolean isDailyMenu(String menu_piatto) {
        if(menu_piatto.equals(UsefulStrings.DAILY_MENU_NAME)){
            System.out.println(UsefulStrings.INVALID_DAILY_MENU);
            return true;
        }
        return false;
    }

    /**
     * Metodo che controlla se i coperti raggiunti, estesi al parametro resCover in input, superi
     * la capienza totale del ristorante.
     *
     * @param resCover parametro da sommare alla totalità dei coperti raggiunti.
     */
    public boolean exceedsCover(int resCover){
        int partialSum = getCopertiRaggiunti() + resCover;
        int postiDispondibili = (int)getCovered() - getCopertiRaggiunti();

        if(partialSum > (int)getCovered()){
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
     * @param itemName, nome dell'item
     * @param itemCover, numero coperti dell'item
     */
    public boolean controlIfExceedsRestaurantWorkload(String itemName, int itemCover){
        double partialSum = getCaricoRaggiunto() + calculateWorkload(itemName, itemCover);

        if(partialSum > getRestaurantWorkload()){
            double workloadRestante = getRestaurantWorkload() - getCaricoRaggiunto();
            System.out.println(UsefulStrings.WORKLOAD_EXCEEDED_AVAILABLE + workloadRestante + "\n\n");
            return true;
        }

        return false;
    }

    /**
     * Metodo che controlla se il carico di lavoro sostenibile del ristorante non è stato superato.
     */
    public boolean workloadRestaurantNotExceeded(){
        double workloadRimanente = getRestaurantWorkload() - this.getCaricoRaggiunto();
        if(this.getCaricoRaggiunto() >= getRestaurantWorkload() || workloadRimanente < getMinimumWorkload()){
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
    public boolean isMenu(String menu_piatto) {
        for (Course c : getMenu()) {
            if(menu_piatto.equals(c.getName()))
                return true;
        }

        return false;
    }

    /**
     * Metodo che si occupa del salvataggio delle prenotazioni nell'apposito archivio.
     */
    public void saveInReservationArchive(){
        agent.getReservationArchiveRepository().save(RestaurantDates.workingDay.format(RestaurantDates.formatter));
    }

    public boolean controlIfAskItemNameAgain(String menu_piatto, ItemList itemList){
        return !isInMenu(menu_piatto) ||
                isAlreadyIn(menu_piatto, itemList.getItemsName()) ||
                controlIfExceedsRestaurantWorkload(menu_piatto, 1);
    }
}
