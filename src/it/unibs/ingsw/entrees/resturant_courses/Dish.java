package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.xml_utils.Parsable;
import java.util.ArrayList;

/**
 * Ha una denominazione precisa, stabilita dal gestore e
 * destinata al consumatore, cioè tale denominazione è
 * quella che compare nei menu. Può non essere disponibile
 * per tutto l’arco dell’anno. Corrisponde a una ricetta,
 * quella usata per cucinarlo, questa corrispondenza è ad
 * uso degli addetti ai lavori entro il ristorante mentre
 * è invisibile ai clienti.
 *
 * @see Parsable
 */
public class Dish implements Parsable {
    /**
     * Nome del piatto.
     */
    private String name;
    /**
     * Periodo di validità del piatto.
     */
    private String availability;
    /**
     * Numeratore del carico di lavoro del piatto.
     */
    private int numerator;
    /**
     * Numeratore del carico di lavoro del piatto..
     */
    private int denominator;

    public static final String START_STRING = "dish";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili di istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("availability");
        ATTRIBUTE_STRINGS.add("n");
        ATTRIBUTE_STRINGS.add("d");
    }


    /**
     * Metodo necessario perché {@code Dish} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setAvailability);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setNumerator);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::setDenominator);
    }

    @Override
    /**
     * Metodo per settare il nome del piatto.
     * @return name nome del piatto.
     */
    public String getStartString() {return START_STRING;}

    // Setters
    /**
     * Metodo per settare il nome del piatto.
     * @param name nome del piatto.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Metodo per settare il periodo di validità del piatto.
     * @param availability periodo di validità del piatto.
     */
    public void setAvailability(String availability) {this.availability = availability;}

    /**
     * Metodo per settare il numeratore del carico di lavoro del piatto.
     * @param numerator numeratore del carico di lavoro del piatto.
     */
    public void setNumerator(String numerator) {this.numerator = Integer.parseInt(numerator);}

    /**
     * Metodo per settare il denominatore del carico di lavoro del piatto.
     * @param denominator denominatore del carico di lavoro del piatto.
     */
    public void setDenominator(String denominator) {this.denominator = Integer.parseInt(denominator);}

    // Getters
    /**
     * Metodo che ritorna il nome del piatto.
     * @return {@link #name} il nome del piatto.
     */
    public String getName() {return name;}

    /**
     * Metodo che ritorna il periodo di validità del piatto.
     * @return {@link #availability} il periodo di validità del piatto.
     */
    public String getAvailability() {return availability;}

    /**
     * Metodo che ritorna il numeratore del carico di lavoro del piatto.
     * @return {@link #numerator} numeratore del carico di lavoro del piatto.
     */
    public int getNumerator() {return numerator;}

    /**
     * Metodo che ritorna il denominatore del carico di lavoro del piatto.
     * @return {@link #denominator} denominatore del carico di lavoro del piatto.
     */
    public int getDenominator() {return denominator;}

    /**
     * Metodo che ritorna la frazione del carico di lavoro del piatto come stringa.
     * @return la frazione del carico di lavoro del piatto.
     */
    public String getWorkload() {return numerator + "/" + denominator;}

    /**
     * Metodo che ritorna la frazione del carico di lavoro del piatto.
     * @return la frazione del carico di lavoro del piatto.
     */
    public Fraction getWorkloadFraction() {return new Fraction(numerator, denominator);}
}
