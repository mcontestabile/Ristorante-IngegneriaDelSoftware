package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;
import java.util.ArrayList;
import java.util.List;

/**
 * Metodo che serve per memorizzare le informazioni da scrivere nel file XML dei carichi di lavoro di piatti e menù tematici.
 *
 * @see Writable
 */
public class WorkloadOfTheDay implements Writable {
    private String name; // nome menu-piatto
    private String type; // se è un menù oppure un piatto.
    private Fraction workload;
    public static final String START_STRING = "today";
    private static final List<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    public WorkloadOfTheDay(String name, String type, Fraction workload) {
        this.name = name;
        this.type = type;
        this.workload = workload;
    }

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

    @Override
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getType);
        getters.put(ATTRIBUTE_STRINGS.get(2), this::getNumerator);
        getters.put(ATTRIBUTE_STRINGS.get(3), this::getDenominator);
    }

    /**
     * Metodo che ritorna il nome della rcetta/del menù.
     * @return {@link #name} il nome della rcetta/del menù.
     */
    public String getName() {
        return name;
    }

    /**
     * Metodo che ritorna il denominatore del carico di lavoro.
     * @return {@link #type} il denominatore del carico di lavoro.
     */
    public String getType() {
        return type;
    }

    /**
     * Metodo che ritorna il numeratore del carico di lavoro.
     * @return il numeratore del carico di lavoro.
     */
    public String getNumerator() {return Integer.toString(workload.getNumerator());}

    /**
     * Metodo che ritorna il denominatore del carico di lavoro.
     * @return il denominatore del carico di lavoro.
     */
    public String getDenominator() {return Integer.toString(workload.getDenominator());}

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
        return XMLTags;
    }
}