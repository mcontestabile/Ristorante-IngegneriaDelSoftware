package it.unibs.ingsw.entrees.drinks;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;

/**
 * Bevanda è un oggetto costituita da due attributi, bevanda e quantità,
 * La coppia esprime la quantità, un valore reale la cui unità di misura
 * è il litro, di bevanda tipicamente consumata, secondo una stima cautelativa,
 * da parte di una persona durante un pasto, dove si assume che una persona
 * durante il pasto possa bere tutti i tipi di bevande proposti dal ristorante.
 *
 * È un parametro inizializzato dal gestore, costituito da un elenco di
 * coppie (bevanda, quantità), una coppia per ciascuna bevanda appartenente
 * all’insieme delle bevande. Ciascuna coppia esprime la quantità, un
 * valore reale la cui unità di misura è il litro, di bevanda tipicamente
 * consumata, secondo una stima cautelativa, da parte di una persona durante
 * un pasto, dove si assume che una persona durante il pasto possa bere
 * tutti i tipi di bevande proposti dal ristorante. A ciascun gruppo di persone,
 * corrispondente a una singola prenotazione, verrà servita ai tavoli una quantità
 * (in litri) di ciascuna bevanda appartenente all’insieme delle bevande pari
 * (al più) al prodotto della quantità di tale bevanda che compare nel parametro
 * consumo pro capite di bevande per il numero di coperti corrispondente a tale
 * prenotazione, arrotondato all’intero superiore.
 *
 * @see Parsable
 */
public class Drink implements Parsable {
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
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("quantity");
    }


    /**
     * Metodo necessario, siccome {@code Drink} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
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
     * Metodo per settare il nome della bevanda.
     * @param name nome della bevanda.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Metodo per settare il consumo pro capite, in L, della bevanda.
     * @param quantity il consumo pro capite, in L, della bevanda.
     */
    public void setQuantity(String quantity) {this.quantity = Double.parseDouble(quantity);}

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

    /**
     * Metodo che ritorna il consumo pro capite, in L, della bevanda.
     * @return {{@link #quantity}} il consumo pro capite, in L, della bevanda.
     */
    public double getQuantityDouble() {return quantity;}
}
