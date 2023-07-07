package it.unibs.ingsw.entrees.appetizers;

import it.unibs.ingsw.entrees.drinks.DrinksMenu;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che permette di aggiornare il file XML dei generi alimentari (extra).
 *
 * @see Writable
 */
public class Starter implements Writable {
    private String genre;
    private Double quantity;

    public static final String START_STRING = "starter";
    private static final List<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("genre");
        ATTRIBUTE_STRINGS.add("quantity");
    }

    /**
     * Costruttore, accetta i parametri caratterizzanti un genere alimentare (extra).
     * @param name nome del genere alimentare (extra).
     * @param quantity consumo pro capite, in hg, genere alimentare (extra).
     */
    public Starter(String name, Double quantity) {
        this.genre = name;
        this.quantity = quantity;
    }

    @Override
    /**
     * Metodo che serve per settare i valori dei singoli tag del genere alimentare (extra).
     */
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getGenre);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getQuantity);
    }

    // Setters
    /**
     * Metodo che setta il nome del genere alimentare (extra).
     * @param genre nome del genere alimentare (extra).
     */
    public void setGenre(String genre) {this.genre = genre;}

    /**
     * Metodo che setta il consumo pro capite, in hg, del genere alimentare (extra).
     * @param quantity consumo pro capite del genere alimentare (extra).
     */
    public void setQuantity(Double quantity) {this.quantity = quantity;}

    // Getters

    /**
     * Metodo che ritorna il nome del genere alimentare (extra).
     * @return {@link #genre} il nome del genere alimentare (extra).
     */
    public String getGenre() {return genre;}

    /**
     * Metodo che ritorna il consumo pro capite del genere alimentare (extra).
     * @return {@link #quantity} il consumo pro capite del genere alimentare (extra).
     */
    public String getQuantity() {return Double.toString(quantity);}

    @Override
    /**
     * Metodo che ritorna il tag di apertura.
     * @return {@link #START_STRING} il tag di apertura.
     */
    public String getTagName() {return START_STRING;}

    @Override
    /**
     * Metodo che ritorna i tag.
     * @return i tag.
     */
    public String[] getAttributeStrings() {return ATTRIBUTE_STRINGS.toArray(new String[0]);}

    @Override
    /**
     * Metodo che ritorna il tag di apertura come stringa.
     * @return il tag di apertura.
     */
    public String[] getChildTagStrings() {return new String[]{Starter.START_STRING};}

    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        XMLTags.add(new XMLTag(Starter.START_STRING, new XMLAttribute("name", getGenre()), new XMLAttribute("quantity", getQuantity())));
        return XMLTags;
    }
}