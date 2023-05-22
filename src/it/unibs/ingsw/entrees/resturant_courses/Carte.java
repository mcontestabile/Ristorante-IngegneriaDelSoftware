package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;

/**
 * Classe che serve ad aggiornare il file XML dei menù.
 *
 * @see Writable
 */
public class Carte implements Writable {
    /**
     * Nome del menù.
     */
    private String name;
    /**
     * Tipo del menù, alla carta o tematico.
     */
    private String type;
    /**
     * Periodo di validità del menù.
     */
    private String validation;
    /**
     * Piatti che compongono il menù.
     */
    private ArrayList<Dish> dishes;
    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "course";
    /**
     * Lista degli attributi.
     */
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /**
     * Costruttore, accetta i parametri caratterizzanti un menù.
     * @param name nome del menù.
     * @param type tipo di menù, alla carta o tematico.
     * @param validation periodo di validità del menù.
     * @param dishes piatti che compongono il menù.
     */
    public Carte(String name, String type, String validation, ArrayList<Dish> dishes) {
        this.name = name;
        this.type = type;
        this.validation = validation;
        this.dishes = dishes;
    }

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("type");
        ATTRIBUTE_STRINGS.add("validation");
    }

    @Override
    /**
     * Metodo che serve per settare i valori dei singoli tag del genere alimentare (extra).
     */
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getType);
        getters.put(ATTRIBUTE_STRINGS.get(2), this::getValidation);
    }

    /**
     * Metodo che ritorna il nome del menù.
     * @return {@link #name} il nome del menù.
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo che ritorna il tipo del menù.
     * @return {@link #type} il tipo del menù.
     */
    public String getType() {
        return type;
    }

    /**
     * Metodo che ritorna periodo di validità del menù.
     * @return {@link #validation} il periodo di validità del menù.
     */
    public String getValidation() {return validation;}

    /**
     * Metodo che ritorna i piatti del menù.
     * @return {@link #dishes} i piatti del menù.
     */
    public ArrayList<Dish> getDishes() {
        return dishes;
    }

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
     * Metodo che ritorna i tag.
     * @return i tag.
     */
    public String[] getAttributeStrings() {
        return ATTRIBUTE_STRINGS.toArray(new String[0]);
    }

    @Override
    /**
     * Metodo che ritorna il tag di apertura come stringa.
     * @return il tag di apertura.
     */
    public String[] getChildTagStrings() {
        return new String[]{Carte.START_STRING};
    }

    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        for (Dish d : dishes)
            XMLTags.add(new XMLTag("dish", new XMLAttribute("has", d.getName())));

        return XMLTags;
    }
}