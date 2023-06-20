package it.unibs.ingsw.users;

import it.unibs.ingsw.mylib.xml_utils.Parsable;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;

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
        return new ArrayList<T>(parser.parseXML(c));
    }

    /**
     * Writing del ricettario per aggiornare l'XML con la nuova ricetta.
     *
     * @param obj l'oggetto da scrivere, compresa quello appena aggiunto.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public <T extends Writable> void writingTask(ArrayList<T> obj, Class<T> c, String path, String tag) throws XMLStreamException {
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
}
