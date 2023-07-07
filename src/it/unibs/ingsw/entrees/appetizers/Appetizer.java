package it.unibs.ingsw.entrees.appetizers;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che identifica un genere alimentare extra,
 * quali pane e grissini, salvati in un file XML.
 *
 * @see Parsable
 */
public class Appetizer implements Parsable {
    /**
     * Nome del genere (alimentare) extra.
     */
    private String genre;
    /**
     * Consumo pro capite del genere alimentare extra.
     */
    private Double quantity;

    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "starter";
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
        ATTRIBUTE_STRINGS.add("genre");
        ATTRIBUTE_STRINGS.add("quantity");
    }


    /**
     * Metodo necessario perché {@code Appetizer} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setGenre);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setQuantity);
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
     * Metodo per settare il nome del genere alimentare (extra).
     * @param genre nome del genere alimentare (extra).
     */
    public void setGenre(String genre) {this.genre = genre;}

    /**
     * Metodo per settare la consumo pro capite, in hg, del genere alimentare (extra).
     * @param quantity consumo pro capite del genere alimentare (extra).
     */
    public void setQuantity(String quantity) {this.quantity = Double.parseDouble(quantity);}

    // Getters
    /**
     * Metodo che ritorna il nome del genere alimentare (extra).
     * @return {@link #genre} il nome del genere alimentare (extra).
     */
    public String getGenre() {return genre;}

    /**
     * Metodo che ritorna il consumo pro capite del genere alimentare (extra) in formato stringa.
     * @return {@link #quantity} il consumo pro capite del genere alimentare (extra) in formato stringa.
     */
    public String getQuantity() {return Double.toString(quantity);}

    /**
     * Metodo che ritorna il consumo pro capite del genere alimentare (extra) in formato decimale.
     * @return {@link #quantity} il consumo pro capite del genere alimentare (extra) in formato decimale.
     */
    public double getQuantityDouble() {return quantity;}
}
