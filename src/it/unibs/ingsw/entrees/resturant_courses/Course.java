package it.unibs.ingsw.entrees.resturant_courses;

import it.unibs.ingsw.mylib.xml_utils.Parsable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe statica, serve per elaborare le informazioni contenute nel file .xml,
 * che contiene gli utenti autorizzati ad accedere a Ristorante.
 * Realisticamente, dovrebbe essere rispettata una condizione di congruenza logica
 * secondo cui i menu tematici, in ciascuna data in cui sono validi, devono contenere
 * (solo) piatti che appartengono al menu alla carta valido nella medesima data.
 *
 * @see Parsable
 */
public class Course implements Parsable {
    /**
     * Nome del menù.
     */
    private String name;
    /**
     * Tipo del menù.
     */
    private String type;
    /**
     * Periodo di validità.
     */
    private String validation;
    /**
     * Piatti del menù.
     */
    private List<String> dishes = new ArrayList<>();

    /**
     * Tag di apertura.
     */
    public static final String START_STRING = "course";
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
        ATTRIBUTE_STRINGS.add("type");
        ATTRIBUTE_STRINGS.add("validation");
        ATTRIBUTE_STRINGS.add("has");
    }


    /**
     * Metodo necessario perché {@code Course} implementa Parsable.
     */
    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setType);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setValidation);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::addDishes);
    }

    @Override
    /**
     * Metodo che ritorna il tag di apertura come stringa.
     * @return {@link #START_STRING} il tag di apertura.
     */
    public String getStartString() {
        return START_STRING;
    }

    // Setters
    /**
     * Metodo per settare il nome del menù.
     * @param name nome della ricetta.
     */
    public void setName(String name) {this.name = name;}

    /**
     * Metodo per settare il tipo del menù.
     * @param type tipo del menù.
     */
    public void setType(String type) {this.type = type;}

    /**
     * Metodo per settare il periodo di validità del menù.
     * @param validation periodo di validità del menù.
     */
    public void setValidation(String validation) {this.validation = validation;}

    /**
     * Metodo per aggiungere piatti al menù.
     * @param dish piatto da aggiungere al menù.
     */
    public void addDishes(String dish) {dishes.add(dish);}

    // Getters
    /**
     * Metodo che ritorna il nome del menù.
     * @return {@link #name} il nome del menù.
     */
    public String getName() {return name;}

    /**
     * Metodo che ritorna il tipo del menù.
     * @return {@link #type} il tipo del menù.
     */
    public String getType() {return type;}

    /**
     * Metodo che ritorna periodo di validità del menù.
     * @return {@link #validation} periodo di validità del menù.
     */
    public String getValidation() {return validation;}

    /**
     * Metodo che ritorna i piatti del menù come stringa.
     * @return i piatti del menù come stringa.
     */
    public String getDishes() {return dishes.toString();}

    /**
     * Metodo che ritorna i piatti del menù.
     * @return {@link #dishes} i piatti del menù.
     */
    public List<String> getDishesArraylist() {return dishes;}
}