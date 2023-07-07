package it.unibs.ingsw.users.manager;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.resturant_courses.*;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.User;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe rappresentativa del gestore, il quale inizializza e visualizza i dati di configurazione
 * relativi al ristorante, crea e visualizza il ricettario e i menu.
 */
public class Manager extends User {
    /*
     * In tutti gli oggetti del ristorante, ci sono varie implementazioni, quelli che
     * sono oggetti di tipo Writable sono per la scrittura, in caso di aggiornamento
     * del corrispettivo file contenente le informazioni, quelli che sono oggetti di
     * tipo Parsable servono per avere le informazioni del file e manipolarle, mentre
     * gli oggetti di tipo Parsable, ma stavolta in HashMap, sono un'alternativa al
     * formato ArrayList per operare più rapidamente, in quanto ArrayList è di tipo
     * o(n), mentre HashMap quando cicla è o(1) — in caso di numerosi oggetti, ipotizziamo
     * un ristorante che raccoglie informazioni di ricette ecc da anni, chiaramente
     * ciclarvi richiede più tempo con ArrayList rispetto a HashMap.
     */

    /**
     * Carichi di lavoro di piatti e menù tematici del giorno.
     */
    private final List<WorkloadOfTheDay> workloads = new ArrayList<>();

    /**
     * Costruttore dell'oggetto gestore. Quando inizializzato, esso deve recuperare
     * le informazioni contenute nei file .xml di sua competenza.
     *
     * @param username l'username del gestore.
     * @param password la password del gestore.
     * @param canIWork attributo che determina se il gestore quel giorno ha già
     *                 inizializzato il ristorante o meno.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public Manager(String username, String password, boolean canIWork) {
        super(username, password, canIWork);
    }

    public void setRestaurant()  throws XMLStreamException {
        setCookbook(parsingTask(UsefulStrings.COOKBOOK_FILE, CookbookRecipe.class));
        setDishes(parsingTask(UsefulStrings.DISHES_FILE, Dish.class));
        setMenu(parsingTask(UsefulStrings.COURSES_FILE, Course.class));
        setDrinks(parsingTask(UsefulStrings.DRINKS_FILE, Drink.class));
        setAppetizers(parsingTask(UsefulStrings.APPETIZERS_FILE, Appetizer.class));
    }

    public List<WorkloadOfTheDay> getWorkloads() {return workloads;}
}