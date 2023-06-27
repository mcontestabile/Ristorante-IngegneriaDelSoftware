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

    public void setCaricoRaggiunto(double newCaricoRaggiunto) {
        this.caricoRaggiunto = newCaricoRaggiunto;
    }

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
     * Metodo che restituisce il carico di lavoro attualmente raggiunto.
     *
     * @return carico_raggiunto
     */
    public double getCaricoRaggiunto() {
        return caricoRaggiunto;
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

    @Override
    public ArrayList<Course> getMenu() {
        return menu;
    }
}
