package it.unibs.ingsw.users.registered_users;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.mylib.xml_utils.Parsable;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class User {
    /**
     * Username.
     */
    private String username;
    /**
     * Password.
     */
    private String password;
    /**
     * Variabile che determina se l'utente può lavorare o meno.
     */
    private boolean canIWork;
    /**
     * Variabile che determina i coperti del ristorante.
     */
    private int covered;
    /**
     * Carico di lavoro per persona.
     */
    private int workloadPerPerson;
    /**
     * Carico di lavoro per persona.
     */
    private double restaurantWorkload;
    /**
     * Menù parsati formato ArrayList.
     */
    private ArrayList<Course> menu;
    /**
     * Menù parsati formato HashMap.
     */
    private HashMap<String, Course> coursesMap;

    // permette di istanziare un oggetto di tipo corpo celeste
    public User(String username, String password, boolean didIWork) {
        this.username = username;
        this.password = password;
        this.canIWork = didIWork;
    }

    /**
     * Parsing del file xml necessario.
     */
    public <T extends Parsable> ArrayList<T> parsingTask(String file, Class<T> c) throws XMLStreamException {
        XMLParser parser = new XMLParser(file);
        return new ArrayList<>(parser.parseXML(c));
    }

    /**
     * Writing del ricettario per aggiornare l'XML con la nuova ricetta.
     *
     * @param obj l'oggetto da scrivere, compresa quello appena aggiunto.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public <T extends Writable> void writingTask(ArrayList<T> obj, String path, String tag) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(path);
        writer.writeArrayListXML(obj, tag);
    }

    /**
     * Metodo che ritorna l'username.
     * @return l'username.
     */
    public String getUsername() {return username;}

    /**
     * Metodo per settare l'username.
     * @param username username dell'utente.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Metodo che ritorna la password.
     * @return la password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Metodo per settare la password.
     * @param password password dell'utente.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Metodo che ritorna se il gestore ha lavorato o meno.
     * @return true se ha lavorato, false altrimenti.
     */
    public boolean isCanIWork() {return canIWork;}

    /**
     * Metodo per settare se il gestore ha lavorato o meno.
     * @param canIWork variabile per indicare se il gestore ha lavorato o meno.
     */
    public void setCanIWork(boolean canIWork) {this.canIWork = canIWork;}

    /**
     * Metodo che ritorna i coperti.
     * @return i coperti.
     */
    public int getCovered() {return covered;}

    /**
     * Metodo per settare i coperti.
     * @param covered  i coperti.
     */
    public void setCovered(int covered) {this.covered = covered;}

    /**
     * Metodo che ritorna il carico di lavoro per persona.
     * @return il carico di lavoro per persona.
     */
    public int getWorkloadPerPerson() {return workloadPerPerson;}

    /**
     * Metodo per settare il carico di lavoro per persona.
     * @param workloadPerPerson il carico di lavoro per persona.
     */
    public void setWorkloadPerPerson(int workloadPerPerson) {this.workloadPerPerson = workloadPerPerson;}

    public double getRestaurantWorkload() {return restaurantWorkload;}

    public void setRestaurantWorkload(double restaurantWorkload) {this.restaurantWorkload = restaurantWorkload;}

    /**
     * Metodo che ritorna i menù.
     * @return i menù.
     */
    public List<Course> getMenu() {return menu;}

    public void setMenu(@NotNull ArrayList<Course> menu) {
        this.menu = menu;
        coursesMap = new HashMap<>();
        menu.forEach(m -> coursesMap.put(m.getName(), m));
    }

    /**
     * Metodo che ritorna l'HashMap dei menù.
     * @return l'HashMap dei menù.
     */
    public Map<String, Course> getCoursesMap() {return coursesMap;}
}
