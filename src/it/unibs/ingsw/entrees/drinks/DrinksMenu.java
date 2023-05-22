package it.unibs.ingsw.entrees.drinks;

import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;

/**
 * Classe che serve per memorizzare i valori con cui aggiornare il file XML delle bevande.
 *
 * @see Writable
 */
public class DrinksMenu implements Writable {
    /**
     * Nome della bevanda.
     */
    private String name;
    /**
     * Consumo pro capite della bevanda.
     */
    private Double quantity;
    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "drink";
    /**
     * Lista degli attributi.
     */
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili di istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("quantity");
    }

    /**
     * Costruttore, accetta i parametri caratterizzanti una bevanda.
     * @param name nome della bevanda.
     * @param quantity consumo pro capite, il L, della bevanda.
     */
    public DrinksMenu(String name, Double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    /**
     * Metodo che serve per settare i valori dei singoli tag del genere alimentare (extra).
     */
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getQuantity);
    }

    // Setters
    /**
     * Metodo per settare il nome della bevanda.
     * @param name nome della bevanda.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Metodo per settare il consumo pro capite, in L, della bevanda.
     * @param quantity il consumo pro capite, in L, della bevanda.
     */
    public void setQuantity(Double quantity) {this.quantity = quantity;}

    // Getters
    /**
     * Metodo che ritorna il nome della bevanda.
     * @return {@link #name} il nome della bevanda.
     */
    public String getName() {return name;}

    /**
     * Metodo che ritorna il consumo pro capite, in L, della bevanda come stringa.
     * @return {@link #quantity} il consumo pro capite, in L, della bevanda.
     */
    public String getQuantity() {return Double.toString(quantity);}

    @Override
    /**
     * Metodo che ritorna il tag di apertura.
     * @return {@link #START_STRING} il tag di apertura.
     */
    public String getTagName() {
        return START_STRING;
    }

    @Override
    /**
     * Metodo che ritorna il tag di apertura.
     * @return il tag.
     */
    public String[] getAttributeStrings() {
        return ATTRIBUTE_STRINGS.toArray(new String[0]);
    }

    @Override
    /**
     * Metodo che ritorna i come stringhe.
     * @return i tag.
     */
    public String[] getChildTagStrings() {
        return new String[]{DrinksMenu.START_STRING};
    }

    @Override
    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        XMLTags.add(new XMLTag(DrinksMenu.START_STRING, new XMLAttribute("name", getName()), new XMLAttribute("quantity", getQuantity())));
        return XMLTags;
    }
}