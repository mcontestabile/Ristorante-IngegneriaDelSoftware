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
    private int copertiRaggiunti;
    /**
     * Carico di lavoro attualmente raggiunto con i menù/piatti delle prenotazioni.
     */
    private double caricoRaggiunto;
    /**
     * La lista delle prenotazioni.
     */
    private ArrayList<ReservationItemList> reservations = new ArrayList<>();
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

        this.copertiRaggiunti = 0; // a inizio giornata quando si crea l'oggetto ReservationAgent
        this.caricoRaggiunto = 0.0; // a inizio giornata quando si crea l'oggetto ReservationAgent
    }


    public ArrayList<ReservationItemList> getReservations() {
        return reservations;
    }

    public ArrayList<Workload> getWorkloads() {
        return workloads;
    }

    public HashMap<String, Workload> getWorkloadsMap() {
        return workloadsMap;
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
     * Metodo che restituisce i coperti del ristorante attualmente raggiunti.
     *
     * @return coperti_raggiunti
     */
    public int getCopertiRaggiunti() {
        return copertiRaggiunti;
    }

    /**
     * Metodo che aggiorna i coperti raggiunti del ristorante.
     * Si somma quindi ai coperti attuali, un nuovo valore, aumentando così di quell'ammontare, il numero di persone attualmente prenotate presso il ristorante.
     *
     * @param nuoviCoperti coperti da sommare agli attuali.
     */
    public void updateCopertiRaggiunti(int nuoviCoperti) {
        this.copertiRaggiunti = this.copertiRaggiunti + nuoviCoperti;
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
     * Metodo che aggiorna il carico di lavoro del ristorante raggiunto.
     * Si somma quindi al carico attuale, un nuovo valore, aumentando così di quell'ammontare, il totale del carico di lavoro attualmente sostenuto dal ristorante.
     * Si effettua poi una trasformazione, richiamando la libreria Math, in modo tale da avere 2 cifre decimali.
     *
     * @param nuovoCarico carico da sommare all'attuale.
     */
    public void updateCaricoRaggiunto(double nuovoCarico) {
        this.caricoRaggiunto = this.caricoRaggiunto + nuovoCarico;

        this.caricoRaggiunto = Math.floor((this.caricoRaggiunto) * 100)/100; // solo due cifre decimali
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
        System.out.println(UsefulStrings.ACTUAL_WORKLOAD_MESSAGE+(this.getCaricoRaggiunto()));
        System.out.println(UsefulStrings.ACTUAL_WORKLOAD_AVAILABLE_MESSAGE+Math.floor((restaurantWorkload - this.getCaricoRaggiunto()) *100)/100+"\n");
    }
}
