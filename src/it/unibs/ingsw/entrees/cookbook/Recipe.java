package it.unibs.ingsw.entrees.cookbook;

import it.unibs.ingsw.entrees.resturant_courses.Dish;
import it.unibs.ingsw.entrees.resturant_courses.NewPlate;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;

/**
 * Classe che serve ad aggiornare il file XML delle Ricette.
 *
 * @see Writable
 */
public class Recipe extends NewPlate implements Writable {
    private ArrayList<String> ingredients;
    private String portion;
    public static final String START_STRING = "recipe";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /**
     * Costruttore, accetta i parametri caratterizzanti una ricetta.
     * @param name nome della ricetta.
     * @param availability periodo di validità della ricetta.
     * @param portions porzioni preparabili dalla ricetta.
     * @param workload carico di lavoro della ricetta.
     * @param ingredients ingredienti della ricetta.
     */
    public Recipe(String name, String availability, String portions, Fraction workload, ArrayList<String> ingredients) {
        super(name, availability, workload);
        this.portion = portions;
        this.ingredients = ingredients;
    }

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili di istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("portions");
        ATTRIBUTE_STRINGS.add("n");
        ATTRIBUTE_STRINGS.add("d");
    }

    @Override
    /**
     * Metodo che serve per settare i valori dei singoli tag del genere alimentare (extra).
     */
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getPortion);
        getters.put(ATTRIBUTE_STRINGS.get(2), this::getNumerator);
        getters.put(ATTRIBUTE_STRINGS.get(3), this::getDenominator);
    }

    /**
     * Metodo che ritorna il numero di porzioni preparabili dalla ricetta.
     * @return {@link #portion} il numero di porzioni preparabili dalla ricetta.
     */
    public String getPortion() {return portion;}

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
     * Metodo che ritorna i tag come stringhe.
     * @return i tag.
     */
    public String[] getChildTagStrings() {
        return new String[]{Recipe.START_STRING};
    }

    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        for (String i : ingredients)
            XMLTags.add(new XMLTag("ingredient", new XMLAttribute("has", i)));

        return XMLTags;
    }
}
