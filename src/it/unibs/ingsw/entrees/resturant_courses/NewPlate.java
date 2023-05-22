package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;

/**
 * Classe che si occupa di memorizzare i valori con cui aggiornare il file XML dei piatti.
 *
 * @see Writable
 */
public class NewPlate implements Writable {
    private String name;
    private String availability;
    private Fraction workload;

    public static final String START_STRING = "dish";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    /**
     * Costruttore, accetta i parametri caratterizzanti un piatto.
     * @param name nome del piatto.
     * @param availability periodo di validità del piatto.
     * @param workload carico di lavoro del piatto.
     */
    public NewPlate(String name, String availability, Fraction workload) {
        this.name = name;
        this.availability = availability;
        this.workload = workload;
    }

    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili d'istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("availability");
        ATTRIBUTE_STRINGS.add("n");
        ATTRIBUTE_STRINGS.add("d");
    }

    @Override
    /**
     * Metodo necessario perché {@code Course} implementa Parsable.
     */
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getAvailability);
        getters.put(ATTRIBUTE_STRINGS.get(2), this::getNumerator);
        getters.put(ATTRIBUTE_STRINGS.get(3), this::getDenominator);
    }

    /**
     * Metodo che ritorna il nome del piatto.
     * @return {@link #name} il nome del piatto.
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo che ritorna il periodo di validità del piatto.
     * @return {@link #availability} periodo di validità del piatto.
     */
    public String getAvailability() {return availability;}

    /**
     * Metodo che ritorna il numeratore del carico di lavoro del piatto.
     * @return numeratore del carico di lavoro del piatto.
     */
    public String getNumerator() {return Integer.toString(workload.getNumerator());}

    /**
     * Metodo che ritorna il denominatore del carico di lavoro del piatto.
     * @return denominatore del carico di lavoro del piatto.
     */
    public String getDenominator() {return Integer.toString(workload.getDenominator());}

    @Override
    /**
     * Metodo che ritorna il tag di apertura.
     * @return il tag di apertura.
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
        return new String[]{NewPlate.START_STRING};
    }

    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();

        XMLTags.add(new XMLTag(NewPlate.START_STRING, new XMLAttribute("name", getName()), new XMLAttribute("availability", getAvailability()),
                new XMLAttribute("n", getNumerator()), new XMLAttribute("d", getDenominator())));
        return XMLTags;
    }
}