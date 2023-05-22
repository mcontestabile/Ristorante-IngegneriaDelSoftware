package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;

/**
 * Metodo che serve per memorizzare le informazioni, prelevate da un file XML,
 * circa i carichi di lavoro dei piatti e dei menù tematici.
 *
 * @see Parsable
 */
public class Workload implements Parsable {
    private String name; // nome menu-piatto
    private String type; // se è un menù oppure un piatto.
    private int numerator;
    private int denominator;
    public static final String START_STRING = "today";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("type");
        ATTRIBUTE_STRINGS.add("n");
        ATTRIBUTE_STRINGS.add("d");
    }


    /**
     * Metodo necessario, siccome {@code Workload} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setType);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setNumerator);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::setDenominator);
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
     * Metodo per settare il nome del menù.
     * @param name nome del menù.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Metodo per settare il tipo di menù, alla carta o tematico.
     * @param type tipo di menù.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Metodo per settare il numeratore del carico di lavoro del menù.
     * @param numerator numeratore del carico di lavoro del menù.
     */
    public void setNumerator(String numerator) {this.numerator = Integer.parseInt(numerator);}

    /**
     * Metodo per settare il denominatore del carico di lavoro del menù.
     * @param denominator denominatore del carico di lavoro del menù.
     */
    public void setDenominator(String denominator) {this.denominator = Integer.parseInt(denominator);}

    // Getters
    /**
     * Metodo che ritorna il nome del menù.
     * @return {@link #name} il nome del menù.
     */
    public String getName() {return name;}

    /**
     * Metodo che ritorna il tipo di menù, alla carta o tematico.
     * @return {@link #type} tipo di menù.
     */
    public String getType() {return type;}

    /**
     * Metodo che ritorna il numeratore del carico di lavoro del menù.
     * @return {@link #numerator} il numeratore del carico di lavoro del menù.
     */
    public int getNumerator() {return numerator;}

    /**
     * Metodo che ritorna il denominatore del carico di lavoro del menù.
     * @return {@link #denominator} il denominatore del carico di lavoro del menù.
     */
    public int getDenominator() {return denominator;}

    /**
     * Metodo che ritorna la frazione del carico di lavoro del menù in formato stringa.
     * @return la frazione del carico di lavoro della ricetta in formato stringa.
     */
    public String getWorkload() {return numerator + "/" + denominator;}

    /**
     * Metodo che ritorna la frazione del carico di lavoro del menù.
     * @return la frazione del carico di lavoro della ricetta.
     */
    public Fraction getWorkloadFraction() {return new Fraction(numerator, denominator);}
}