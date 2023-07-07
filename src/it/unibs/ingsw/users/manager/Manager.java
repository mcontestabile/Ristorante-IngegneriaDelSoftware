package it.unibs.ingsw.users.manager;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.appetizers.Starter;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.drinks.DrinksMenu;
import it.unibs.ingsw.entrees.resturant_courses.*;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.User;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Ricette parsate formato ArrayList.
     */
    private ArrayList<CookbookRecipe> cookbookRecipes;
    /**
     * Ricette da scrivere formato ArrayList.
     */
    private ArrayList<Recipe> recipes;
    /**
     * Ricette parsate formato HashMap.
     */
    private HashMap<String, CookbookRecipe> recipeMap;

    /**
     * Menù da scrivere formato ArrayList.
     */
    private ArrayList<Carte> newCourse;

    /**
     * Bevande parsate formato ArrayList.
     */
    private ArrayList<Drink> drinks;
    /**
     * Bevande da scrivere formato ArrayList.
     */
    private ArrayList<DrinksMenu> newDrinksMenu;
    /**
     * Bevande parsate formato HashMap.
     */
    private HashMap<String, Drink> drinksMap;


    /**
     * Generi alimentari (extra) parsati formato ArrayList.
     */
    private ArrayList<Appetizer> appetizers;
    /**
     * Generi alimentari (extra) da scrivere formato ArrayList.
     */
    private ArrayList<Starter> newAppetizer;
    /**
     * Generi alimentari (extra) parsati formato HashMap.
     */
    private HashMap<String, Appetizer> appetizersMap;

    /**
     * Piatti parsati formato ArrayList.
     */
    private ArrayList<Dish> dishes;
    /**
     * Piatti da scrivere formato ArrayList.
     */
    private ArrayList<NewPlate> newDish;
    /**
     * Piatti parsati formato HashMap.
     */
    private HashMap<String, Dish> dishesMap;

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

    public void setCookbook(@NotNull ArrayList<CookbookRecipe> cookbookRecipes) {
        this.cookbookRecipes = cookbookRecipes;
        recipeMap = new HashMap<>();
        cookbookRecipes.forEach(e -> recipeMap.put(e.getName(), e));
    }

    public void setDishes(@NotNull ArrayList<Dish> dishes) {
        this.dishes = dishes;
        dishesMap = new HashMap<>();
        dishes.forEach(d -> dishesMap.put(d.getName(), d));
    }

    public void setAppetizers(@NotNull ArrayList<Appetizer> appetizers) {
        this.appetizers = appetizers;
        appetizersMap = new HashMap<>();
        appetizers.forEach(a -> appetizersMap.put(a.getGenre(), a));
    }

    public void setDrinks(@NotNull ArrayList<Drink> drinks) {
        this.drinks = drinks;
        drinksMap = new HashMap<>();
        drinks.forEach(d -> drinksMap.put(d.getName(), d));
    }

    /**
     * Metodo che ritorna le bevande.
     * @return le bevande.
     */
    public List<Drink> getDrinks() {return drinks;}

    /**
     * Metodo che ritorna i generi alimentari (extra).
     * @return i generi alimentari (extra).
     */
    public List<Appetizer> getAppetizers() {return appetizers;}

    /**
     * Metodo che ritorna i piatti.
     * @return i piatti.
     */
    public List<Dish> getDishes() {return dishes;}

    /**
     * Metodo che ritorna il ricettario.
     * @return il ricettario.
     */
    public List<CookbookRecipe> getCookbook() {return cookbookRecipes;}

    /**
     * Metodo che ritorna l'HashMap delle bevande.
     * @return l'HashMap delle bevande.
     */
    public Map<String, Drink> getDrinksMap() {return drinksMap;}

    /**
     * Metodo che ritorna l'HashMap dei generi alimentari (extra).
     * @return l'HashMap dei generi alimentari (extra).
     */
    public Map<String, Appetizer> getAppetizersMap() {return appetizersMap;}

    /**
     * Metodo che ritorna l'HashMap delle ricette.
     * @return l'HashMap delle ricette.
     */
    public Map<String, CookbookRecipe> getRecipeMap() {
        return recipeMap;
    }

    /**
     * Metodo che ritorna l'HashMap dei piatti.
     * @return l'HashMap dei piatti.
     */
    public Map<String, Dish> getDishesMap() {return dishesMap;}

    public List<WorkloadOfTheDay> getWorkloads() {return workloads;}

    public List<Recipe> getRecipes() {return recipes;}

    public List<Carte> getNewCourse() {return newCourse;}

    public List<DrinksMenu> getNewDrinksMenu() {return newDrinksMenu;}

    public List<Starter> getNewAppetizer() {return newAppetizer;}

    public List<NewPlate> getNewDish() {return newDish;}

    public List<CookbookRecipe> getCookbookRecipes() {return cookbookRecipes;}
}