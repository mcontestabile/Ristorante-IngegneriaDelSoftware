package it.unibs.ingsw.entrees.cookbook;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che identifica una ricetta, salvata in un file XML.
 *
 * @see Parsable
 */
public class CookbookRecipe implements Parsable {
    /**
     * Nome della ricetta.
     */
    private String name;
    /**
     * Porzioni producibili.
     */
    private int portion;
    /**
     * Numeratore del carico di lavoro.
     */
    private int numerator;
    /**
     * Denominatore del carico di lavoro.
     */
    private int denominator;
    /**
     * Ingredienti della ricetta.
     */
    private List<String> ingredients = new ArrayList<>();

    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "recipe";
    /**
     * Lista degli attributi.
     */
    private static final List<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("portions");
        ATTRIBUTE_STRINGS.add("n");
        ATTRIBUTE_STRINGS.add("d");
        ATTRIBUTE_STRINGS.add("has");
    }


    /**
     * Metodo necessario, perché {@code CookbookRecipe} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setPortion);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setNumerator);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::setDenominator);
        setters.put(ATTRIBUTE_STRINGS.get(4), this::addIngredients);
    }

    @Override
    /**
     * Metodo che ritorna il tag di apertura.
     */
    public String getStartString() {
        return START_STRING;
    }

    // Setters
    /**
     * Metodo per settare il nome della ricetta.
     * @param name nome della ricetta.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Metodo per settare il numero di porzioni preparabili dalla ricetta.
     * @param portion numero di porzioni preparabili dalla ricetta.
     */
    public void setPortion(String portion) {this.portion = Integer.parseInt(portion);}

    /**
     * Metodo per settare il numeratore del carico di lavoro della ricetta.
     * @param numerator numeratore del carico di lavoro della ricetta.
     */
    public void setNumerator(String numerator) {this.numerator = Integer.parseInt(numerator);}

    /**
     * Metodo per settare il denominatore del carico di lavoro della ricetta.
     * @param denominator denominatore del carico di lavoro della ricetta.
     */
    public void setDenominator(String denominator) {this.denominator = Integer.parseInt(denominator);}

    /**
     * Metodo per settare gli ingredienti della ricetta.
     * @param ingredient un ingrediente della ricetta.
     */
    public void addIngredients(String ingredient) {ingredients.add(ingredient);}

    // Getters
    /**
     * Metodo che ritorna il nome della ricetta.
     * @return {@link #name} il nome della ricetta.
     */
    public String getName() {return name;}

    /**
     * Metodo che ritorna il numero di porzioni preparabili dalla ricetta come stringa.
     * @return {@link #portion} il numero di porzioni preparabili dalla ricetta come stringa.
     */
    public String getPortion() {return Integer.toString(portion);}

    /**
     * Metodo che ritorna il numero di porzioni preparabili dalla ricetta.
     * @return {@link #portion} il numero di porzioni preparabili dalla ricetta.
     */
    public int getPortionInt() {return portion;}

    /**
     * Metodo che ritorna il numeratore del carico di lavoro della ricetta.
     * @return {@link #numerator} il numeratore del carico di lavoro della ricetta.
     */
    public int getNumerator() {return numerator;}

    /**
     * Metodo che ritorna il denominatore del carico di lavoro della ricetta.
     * @return {@link #denominator} il denominatore del carico di lavoro della ricetta.
     */
    public int getDenominator() {return denominator;}

    /**
     * Metodo che ritorna la struttura dati, l'ArrayList, che memorizza gli ingredienti.
     * @return {@link #ingredients} gòi ingredienti.
     */
    public List<String> getIngredients() {return ingredients;}

    /**
     * Metodo che ritorna gli ingredienti della ricetta in formato stringa.
     * @return gli ingredienti della ricetta in formato stringa.
     */
    public String getIngredientsToString() {return ingredients.toString();}

    /**
     * Metodo che ritorna la frazione del carico di lavoro della ricetta in formato stringa.
     * @return la frazione del carico di lavoro della ricetta in formato stringa.
     */
    public String getWorkload() {return numerator + "/" + denominator;}
}
