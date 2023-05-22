package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.entrees.appetizers.Starter;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.Parsable;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Classe istanziabile. Permette di creare oggetti di
 * tipo articolo.
 */
public class Article implements Writable {

    /**
     * Nome dell'articolo.
     */
    private String name;
    /**
     * Quantità dell'articolo.
     */
    private double quantity;
    /**
     * Unità di misura dell'articolo.
     */
    private String measure;

    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "article";
    /**
     * Lista degli attributi.
     */
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    // Questo oggetto consente il calcolo del tempo trascorso.
    Timer timer;

    /**
     * Costruttore dell'oggetto articolo
     * @param name nome dell'articolo.
     * @param quantity quantita dell'articolo.
     * @param measure unità di misura.
     */
    public Article(String name, double quantity, String measure) {
        this.name = name;
        this.quantity = quantity;
        this.measure = measure;
    }


    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("quantity");
        ATTRIBUTE_STRINGS.add("measure");
    }

    /*
     * Getters e setters.
     */
    public String getName() {
        return name;
    }

    public String getMeasure() {return measure;}

    public void setName(String name) {
        this.name = name;
    }
    public void setMeasure(String measure) {this.measure = measure;}
    public void setQuantity(String quantity) {this.quantity = Double.parseDouble(quantity);}

    public String getQuantityString() {return Double.toString(quantity);}
    public double getQuantity() {return this.quantity;}


    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }


    /**
     * Questo metodo genera le nuove unità richieste dal magazzino,
     * aggiungendole alle unità attualmente presenti nel magazzino.
     *
     * @param quantity quantità.
     */
    public void incrementQuantity(double quantity) {
        this.quantity+=quantity;
    }

    public void decrementQuantity(double quantity) {
        this.quantity -= quantity;
        if(this.quantity<0) this.quantity = 0;
    }

    /**
     * Questo metodo permette di settare la quantità
     * iniziale di ciascun oggetto di tipo {@code Articolo}.
     * @param maximum massima quantità possibile.
     * @param minimum minima quantità possibile.
     * @return quantità iniziale.
     */
    public int settingQuantity(int maximum, int minimum) {
        //return RandomNumbers.obtainInt(maximum, minimum);
        return 0;
    }

    /**
     * Controllo se il nome dell'oggetto {@code Articolo} ricercato
     * esiste davvero o meno. Il metodo è pensato per un riuso, nel
     * caso venga implementata la possibilità all'utente di inserire
     * nuovi articoli nel magazzino (nel caso il programma venga
     * usato da un'azienda di logistica).
     * @param n nome inserito dall'utente nella ricerca.
     * @return se esiste true, se non esiste false.
     */
    public boolean checkNameAvailability(String n) {
        if (name.equalsIgnoreCase(n)) return true;
        return false;
    }

    @Override
    public String toString() {
        return "Articolo\n" +
                "nome » " + name +
                "\nquantità disponibile » " + quantity + measure +
                "\n ";
    }

    @Override
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getQuantityString);
        getters.put(ATTRIBUTE_STRINGS.get(2), this::getMeasure);
    }

    @Override
    public String getTagName() {
        return START_STRING;
    }

    @Override
    public String[] getAttributeStrings() {
        return ATTRIBUTE_STRINGS.toArray(new String[0]);
    }

    @Override
    public String[] getChildTagStrings() {

        return new String[]{Article.START_STRING};
    }

    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        XMLTags.add(new XMLTag(Article.START_STRING, new XMLAttribute("name", getName()), new XMLAttribute("quantity", getQuantityString()), new XMLAttribute("measure", getMeasure())));
        return XMLTags;
    }


}
