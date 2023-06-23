package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import it.unibs.ingsw.users.registered_users.User;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Math;

/**
 * Classe rappresentativa dell'addetto, il quale
 * permette l'aggiunta delle prenotazioni e il loro salvataggio sul relativo file
 */
public class ReservationsAgent extends User {
    /**
     * Coperti attualmente raggiunti con le prenotazioni.
     */
    private int coperti_raggiunti;
    /**
     * Carico di lavoro attualmente raggiunto con i menù/piatti delle prenotazioni.
     */
    private double carico_raggiunto;
    /**
     * La lista delle prenotazioni.
     */
    private ArrayList<Reservation> reservations = new ArrayList<>();
    /**
     * La lista dei menù con i relativi piatti.
     */
    private ArrayList<Course> menu;
    /**
     * La lista dei workloads di giornata.
     */
    private ArrayList<Workload> workloads;
    /**
     * Map per reperire i carichi di lavoro in maniera pronta e rapida.
     */
    private HashMap<String, Workload> workloadsMap;

    /**
     * Costruttore dell'oggetto addetto. Quando inizializzato, esso deve recuperare
     * le informazioni contenute nei file .xml di sua competenza.
     *
     * @param username username.
     * @param password password.
     * @param canIWork determina se l'addetto alle prenotazioni puà lavorare o meno.
     */
    public ReservationsAgent(String username, String password, boolean canIWork) {
        super(username, password, canIWork);

        this.coperti_raggiunti = 0; // a inizio giornata quando si crea l'oggetto ReservationAgent
        this.carico_raggiunto = 0.0; // a inizio giornata quando si crea l'oggetto ReservationAgent
    }

    /**
     * Metodo che inserisce una Reservation e la scrive nell'agenda.
     *
     * @param name nome prenotazione.
     * @param resCover coperti prenotazione.
     * @param itemList lista di item (menù/piatti) di una prenotazione.
     */
    public void insertReservation(String name, String resCover, HashMap<String, String> itemList) {

        this.reservations.add(new Reservation(name,resCover,itemList));

        try {
            agendaWritingTask(reservations);
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
    public void agendaWritingTask(ArrayList<Reservation> reservations) throws XMLStreamException{
        XMLWriter writer = new XMLWriter(UsefulStrings.AGENDA_FILE);
        writer.writeArrayListXML(reservations, UsefulStrings.AGENDA_OUTER_TAG);
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    /**
     * Metodo che fornisce l'elenco dei nomi presenti nell'elenco prenotazioni dell'addetto.
     * Utile in fase di controllo per evitare l'inserimento di nomi duplicati.
     *
     * @return names
     */
    public ArrayList<String> getReservationNameList(){
        ArrayList<String> names = new ArrayList<>();

        for(Reservation r : reservations){
            names.add(r.getName());
        }
        return names;
    }

    /**
     * Metodo che ritorna il menù disponibile.
     *
     * @return menu
     */
    public ArrayList<Course> getMenu(){return menu;}

    /**
     * Metodo per settare il menù al quale l'agent si riferisce.
     *
     * @param menu la lista di piatti.
     */
    public void setMenu(@NotNull ArrayList<Course> menu) {
        this.menu = menu;
    }

    /**
     * Metodo per settare i workloads ai quali l'agent si riferisce.
     * Vengono poi inseriti nella map, per una ricerca più veloce al bisogno.
     *
     * @param workloads la lista di workloads.
     */
    public void setWorkloads(@NotNull ArrayList<Workload> workloads) {
        this.workloads = workloads;
        workloadsMap = new HashMap<>();
        workloads.forEach(w -> workloadsMap.put(w.getName(), w));
    }

    /**
     * Parsing dal file per reperire i vari menu e piatti disponibili.
     *
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public ArrayList<Course> coursesParsingTask() throws XMLStreamException {
        XMLParser coursesParser = new XMLParser(UsefulStrings.COURSES_FILE);
        return new ArrayList<>(coursesParser.parseXML(Course.class));
    }

    /**
     * Parsing dal file per reperire i vari carichi di lavoro dei menu e piatti disponibili.
     *
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public ArrayList<Workload> workloadsParsingTask() throws XMLStreamException {
        XMLParser workloadsParser = new XMLParser(UsefulStrings.WORKLOADS_FILE);
        return new ArrayList<>(workloadsParser.parseXML(Workload.class));
    }

    /**
     * Metodo che restituisce i coperti del ristorante attualmente raggiunti.
     *
     * @return coperti_raggiunti
     */
    public int getCopertiRaggiunti() {
        return coperti_raggiunti;
    }

    /**
     * Metodo che aggiorna i coperti raggiunti del ristorante.
     * Si somma quindi ai coperti attuali, un nuovo valore, aumentando così di quell'ammontare, il numero di persone attualmente prenotate presso il ristorante.
     *
     * @param nuoviCoperti coperti da sommare agli attuali.
     */
    public void updateCopertiRaggiunti(int nuoviCoperti) {
        this.coperti_raggiunti = this.coperti_raggiunti + nuoviCoperti;
    }

    /**
     * Metodo che restituisce il carico di lavoro attualmente raggiunto.
     *
     * @return carico_raggiunto
     */
    public double getCarico_raggiunto() {
        return carico_raggiunto;
    }

    /**
     * Metodo che aggiorna il carico di lavoro del ristorante raggiunto.
     * Si somma quindi al carico attuale, un nuovo valore, aumentando così di quell'ammontare, il totale del carico di lavoro attualmente sostenuto dal ristorante.
     * Si effettua poi una trasformazione, richiamando la libreria Math, in modo tale da avere 2 cifre decimali.
     *
     * @param nuovoCarico carico da sommare all'attuale.
     */
    public void updateCaricoRaggiunto(double nuovoCarico) {
        this.carico_raggiunto = this.carico_raggiunto + nuovoCarico;

        this.carico_raggiunto = Math.floor((this.carico_raggiunto) * 100)/100; // solo due cifre decimali
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
        for (Course c : menu) {
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
     * Metodo che controlla se l'item in input sia un piatto, e non un menu tematico.
     * Se l'item inserito avrà una corrispondenza nell'elenco dei menu tematici, allora si tratterà
     * di un menu, perciò si restituirà false. Altrimenti si tratterà di un piatto normale, quindi
     * si restituirà true.
     *
     * @param menu_piatto nome del menù/piatto da verificare.
     */
    public boolean isDish(String menu_piatto) {
        for (Course c : menu) {
            if(menu_piatto.equals(c.getName()))
                return false;
        }

        return true;
    }

    /**
     * Metodo che salva nell'archivio delle prenotazioni l'agenda della giornata.
     * Questo metodo prende il contenuto dell'agenda e lo salva (nell'archivio prenotazioni) in un file,
     * il cui nome contenente la data relativa del workingDay.
     *
     * @param workingDay giornata lavorativa in formato stringa.
     * @throws IOException nel caso in cui la scrittura su file lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void salvaInArchivioPrenotazioni(String workingDay) throws IOException{
        String f = UsefulStrings.LOCATION_RES_ARCHIVE + workingDay + UsefulStrings.XML_FILE_EXTENSION;

        Scanner myReader = new Scanner(new File(UsefulStrings.AGENDA_FILE));
        FileWriter myWriter = new FileWriter(f);

        boolean firstLineDone = true;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            myWriter.write(data+"\n");

            // i commenti in XML vanno dopo l'intestazione
            // ergo faccio scrivere la prima linea poi imposto la flag come false, in modo tale da non entrare più nell'if
            if(firstLineDone) {
                myWriter.write("<!--Elenco prenotazioni del " + workingDay + "-->\n");
                firstLineDone = false;
            }
        }
        myReader.close();
        myWriter.close();

        System.out.println(UsefulStrings.OK_FILE_SAVED_MESSAGE);
    }

    /**
     * Metodo che calcola il workload dell'item (menù/piatto) passato come parametro, esteso al numero dei coperti che l'hanno selezionato in fase di prenotazione.
     *
     * @param i item (menù/piatto) del quale si vuole sapere il workload.
     * @param cover numero coperti per l'item (menù/piatto) in questione.
     * @return workload dell'item (menù/piatto) esteso al numero dei coperti che l'hanno selezionato in fase di prenotazione.
     */
    public double calculateWorkload(String i, int cover){
        Fraction w = workloadsMap.get(i).getWorkloadFraction();
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
        }while(exceedsCover(resCover, this.getCopertiRaggiunti(), totCover));

        return resCover;
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
                this.exceedsRestaurantWorkload(this.calculateWorkload(menu_piatto, 1), this.getCarico_raggiunto(), restaurantWorkload));


        return menu_piatto;
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
    public boolean moreItems(int nItems, int nMenu, int resCover){
        int sumItemsCover = nItems + nMenu;

        if(sumItemsCover < resCover && nMenu <= resCover)
            return true;
        else
            return DataInput.yesOrNo(UsefulStrings.MORE_ITEMS);
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
     * Metodo che controlla se il carico di lavoro sostenibile del ristorante non è stato superato.
     *
     * @param restaurantWorkload il carico sostenibile del ristorante.
     */
    public boolean workloadRestaurantNotExceeded(double restaurantWorkload){
        double workloadRimanente = restaurantWorkload - this.getCarico_raggiunto();
        if(this.getCarico_raggiunto() >= restaurantWorkload || workloadRimanente < this.getMinimumWorkload()){
            System.out.println(UsefulStrings.NO_MORE_RES_WORKLOAD);
            return false;
        }
        return true;
    }

    /**
     * Metodo che visualizza a video informazioni sui coperti del ristorante.
     *
     * @param totCover capienza massima del ristorante.
     */
    public void seeInfoCovered(int totCover) {
        System.out.println(UsefulStrings.ACTUAL_COVER_MESSAGE+this.getCopertiRaggiunti());
        System.out.println(UsefulStrings.ACTUAL_COVER_AVAILABLE_MESSAGE+(totCover - this.getCopertiRaggiunti())+"\n");
    }

    /**
     * Metodo che visualizza a video informazioni sul carico di lavoro.
     *
     * @param restaurantWorkload il carico sostenibile del ristorante.
     */
    public void seeInfoWorkload(double restaurantWorkload){
        System.out.println(UsefulStrings.ACTUAL_WORKLOAD_MESSAGE+(this.getCarico_raggiunto()));
        System.out.println(UsefulStrings.ACTUAL_WORKLOAD_AVAILABLE_MESSAGE+Math.floor((restaurantWorkload - this.getCarico_raggiunto()) *100)/100+"\n");
    }
}
