package it.unibs.ingsw.users.manager;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.appetizers.Starter;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.drinks.DrinksMenu;
import it.unibs.ingsw.entrees.resturant_courses.*;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.mylib.utilities.RestaurantDates;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe rappresentativa del gestore, il quale inizializza e visualizza i dati di configurazione
 * relativi al ristorante, crea e visualizza il ricettario e i menu.
 */
public class Manager {
    /**
     * Username.
     */
    private String username;
    /**
     * Password.
     */
    private String password;

    /**
     * Formatter della data, serve per ottenerla in formato italiano.
     */
    DateTimeFormatter formatter;
    /**
     * Data del giorno successivo.
     */
    LocalDate tomorrow;
    /**
     * Data del giorno successivo formattata.
     */
    String tomorrowString;

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
     * Menù parsati formato ArrayList.
     */
    private ArrayList<Course> menu;
    /**
     * Menù da scrivere formato ArrayList.
     */
    private ArrayList<Carte> newCourse;
    /**
     * Menù parsati formato HashMap.
     */
    private HashMap<String, Course> coursesMap;


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
    private final ArrayList<WorkloadOfTheDay> workloads = new ArrayList<>();

    /**
     * Carico di lavoro per persona.
     */
    private int workloadPerPerson;
    /**
     * Coperti.
     */
    private int covered;
    /**
     * Variabile che determina se il gestore ha lavorato o meno.
     */
    private boolean didIWork;

    /**
     * Costruttore dell'oggetto gestore. Quando inizializzato, esso deve recuperare
     * le informazioni contenute nei file .xml di sua competenza.
     *
     * @param username l'username del gestore.
     * @param password la password del gestore.
     * @param didIWork attributo che determina se il gestore quel giorno ha già
     *                 inizializzato il ristorante o meno.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public Manager(String username, String password, boolean didIWork) throws XMLStreamException {
        this.username = username;
        this.password = password;
        this.didIWork = didIWork;

        setCookbook(cookbookParsingTask());
        setDishes(dishesParsingTask());
        setMenu(coursesParsingTask());
        setDrinks(drinksParsingTask());
        setAppetizers(appetizersParsingTask());
    }

    /**
     * Metodo che serve per effettuare un controllo fondamentale:
     * il sistema deve verificare autonomamente che i piatti presenti
     * in precedenza nel menù siano validi nel giorno lavorativo successivo.
     * Facciamo un esempio: ieri era ancora inverno ed è disponibile nel menù
     * la ricetta di «vitello tonnato», che ha periodo di validità «inverno»,
     * il vitello tonnato è stata inserito in una data invernale, il che è corretto.
     * Tuttavia, oggi è il primo giorno di primavera e non deve essere assolutamente
     * presente «vitello tonnato», perché la data di validità è altamente scorretta,
     * non corrisponde alla stagione odierna! Di conseguenza, «vitello tonnato» va
     * eliminato sia dal menù alla carta che dall'eventuale/eventuali menù alla carta
     * in cui è stato inserito. Inoltre, si effettueranno controlli per verificare che
     * non ci siano menù tematici, con validità «inverno», in quanto la sua validità è
     * stata settata a suo tempo quando era inverno, situazione che non è più valida.
     * Questo tipo di ragionamento si fa con le stagioni, i giorni della settimana e le date.
     */
    public void checkRestaurantDishesAndCourses() {
        newCourse = new ArrayList<>();

        // Itero in ciascun menù esistente.
        for(Course c : menu) {
            // Se la data di validità del menù è coerente, bisogna controllare all'interno del menù se i suoi piatti lo sono.
            if (RestaurantDates.checkDate(c.getValidation(), tomorrowString, tomorrow)) {
                ArrayList<Dish> courseDishes = new ArrayList<>();

                for (String s : c.getDishesArraylist()) {
                    if (RestaurantDates.checkDate(dishesMap.get(s).getAvailability(), tomorrowString, tomorrow))
                        courseDishes.add(dishesMap.get(s));
                    else continue;
                }

                Fraction f = menuWorkload(courseDishes);
                // Controlliamo che il menu tematico controllato abbia un carico di lavoro ammissibile in base al carico di lavoro di persona del giorno.
                if(c.getType().equalsIgnoreCase(UsefulStrings.THEMED_COURSE) && checkMenuWorkload(f)) {
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    workloads.add(new WorkloadOfTheDay(c.getName(), "menu", f));
                }
                else if(c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE)) {
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    for (Dish d : courseDishes)
                        workloads.add(new WorkloadOfTheDay(d.getName(), "piatto", d.getWorkloadFraction()));
                }
            } else continue;
        }

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    /**
     * Elenca gli ingredienti con le dosi opportune, il numero (intero)
     * di porzioni, che è variabile da una ricetta all’altra, che ne
     * derivano. Una porzione è idonea per essere consumata da una singola
     * persona. Il carico di lavoro per porzione è una frazione,
     * minore dell’unità, del carico di lavoro per persona.
     *
     * @param name         nome della ricetta.
     * @param ingredients  ingredienti della ricetta.
     * @param portions     porzioni prodotte.
     * @param availability periodo in cui il piatto è disponibile (tutto l'anno/una stagione precisa).
     */
    public void insertRecipe(String name, ArrayList<String> ingredients, String availability, int portions, Fraction workloadPerPortion) {
        recipes = new ArrayList<>();

        for (CookbookRecipe r : cookbookRecipes) {
            Fraction workload = new Fraction(r.getNumerator(), r.getDenominator());
            recipes.add(new Recipe(r.getName(), dishesMap.get(r.getName()).getAvailability(), r.getPortion(), workload, r.getIngredients()));
        }

        recipes.add(new Recipe(name, availability, Integer.toString(portions), workloadPerPortion, ingredients));

        try {
            cookbookWritingTask(recipes);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Elenca gli ingredienti con le dosi opportune, il numero (intero) di porzioni, che è variabile da una ricetta all’altra, che ne
     * derivano. Una porzione è idonea per essere consumata da una singola persona. Il carico di lavoro per porzione è una frazione,
     * minore dell’unità, del carico di lavoro per persona.
     *
     * @param name         nome della ricetta.
     * @param availability periodo in cui il piatto è disponibile (tutto l'anno/una stagione precisa).
     */
    public void insertDish(String name, String availability, Fraction workloadPerPerson) {
        newDish = new ArrayList<>();

        for (Dish d : dishes)
            newDish.add(new NewPlate(d.getName(), dishesMap.get(d.getName()).getAvailability(), d.getWorkloadFraction()));

        newDish.add(new NewPlate(name, availability, workloadPerPerson));

        try {
            dishesWritingTask(newDish);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il ricettario sulla base della richiesta del gestore,
     * in questo caso di rimuovere una ricetta sulla base del nome fornito.
     *
     * @param name nome della ricetta da togliere.
     */
    public void removeRecipe(String name) {
        recipes = new ArrayList<>();

        cookbookRecipes.remove(recipeMap.get(name));
        recipeMap.remove(name);

        cookbookRecipes.forEach(r -> recipes.add(new Recipe(r.getName(), dishesMap.get(r.getName()).getAvailability(), r.getPortion(), new Fraction(r.getNumerator(), r.getDenominator()), r.getIngredients())));

        try {
            cookbookWritingTask(recipes);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare i piatti sulla base della richiesta del gestore,
     * in questo caso di rimuovere una ricetta sulla base del nome fornito.
     *
     * @param name nome della piatto da togliere.
     */
    public void removeDish(String name) {
        newDish = new ArrayList<>();

        dishes.remove(dishesMap.get(name));
        dishesMap.remove(name);

        dishes.forEach(d -> newDish.add(new NewPlate(d.getName(), d.getAvailability(), d.getWorkloadFraction())));

        try {
            dishesWritingTask(newDish);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù sulla base della richiesta del gestore, in questo caso
     * di aggiungere un menù tematico sulla base delle informazioni fornite dall'utente.
     *
     * @param name         nome del menù tematico da aggiungere.
     * @param dishes       i piatti da aggiungere al menù tematico.
     * @param availability periodo di validità del menù tematico.
     */
    public void insertCourse(String name, ArrayList<String> dishes, String availability, Fraction menuWorkload) {
        newCourse = new ArrayList<>();

        for (Course c : menu) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist())
                courseDishes.add(dishesMap.get(s));

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        ArrayList<Dish> newMenuDishes = new ArrayList<>();
        dishes.forEach(d -> newMenuDishes.add(dishesMap.get(d)));

        newCourse.add(new Carte(name, UsefulStrings.THEMED_COURSE, availability, newMenuDishes));

        workloads.add(new WorkloadOfTheDay(name, "menu", menuWorkload));

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù sulla base della richiesta del gestore, in questo caso
     * di rimuovere un menù tematico sulla base del nome fornito dall'utente.
     *
     * @param name nome del menù tematico da rimuovere.
     */
    public void removeCourse(String name) {
        newCourse = new ArrayList<>();

        menu.remove(coursesMap.get(name));
        coursesMap.remove(name);

        workloads.removeIf(n -> n.getName().equals(name));

        for (Course c : menu) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist()) {
                courseDishes.add(dishesMap.get(s));
            }

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù sulla base della richiesta del gestore, in questo caso
     * di aggiungere un piatto al menu alla carta sulla base delle informazioni fornite dall'utente.
     *
     * @param name nome del piatto da rimuovere dal menu alla carta.
     */
    public void insertDishInALaCarteCourse(String name) {
        newCourse = new ArrayList<>();

        /*
         * Scopo del codice: prendere e aggiungere il piatto al menu alla carta.
         * Come fare? Ciclo su tutti i menù disponibili con un for, controllo che
         * tipo di menu è, distinguendo le casistiche:
         *
         *         c menu alla carta -> prendo tutte i suoi piatti + aggiungo quello nuovo.
         *           c menu tematico -> è come prima.
         */
        for (Course c : menu) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist())
                courseDishes.add(dishesMap.get(s));

            // controllo il tipo di menù, il piatto va aggiunto SOLO al menù alla carta.
            if (c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE))
                courseDishes.add(dishesMap.get(name)); // aggiungo il nome del piatto che si vuole.

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        workloads.add(new WorkloadOfTheDay(name, "piatto", dishesMap.get(name).getWorkloadFraction()));

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù sulla base della richiesta del gestore, in questo caso
     * di rimuovere un menù tematico sulla base del nome fornito dall'utente.
     *
     * @param name nome del piatto da rimuovere dal menu alla carta.
     */
    public void removeDishInALaCarteCourse(String name) {
        newCourse = new ArrayList<>();

        workloads.removeIf(n -> n.getName().equalsIgnoreCase(name));

        // ATTENZIONE: il piatto potrebbe essere in un menù tematico! Il for deve ciclare anche in quelli!
        for (Course c : menu) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist()) {
                if (!c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE) || !s.equalsIgnoreCase(name))
                    courseDishes.add(dishesMap.get(s));
            }

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù sulla base della richiesta del gestore, in questo caso
     * di rimuovere un menù tematico sulla base del nome fornito dall'utente.
     *
     * @param name nome del menù tematico da rimuovere.
     */
    public void removeDishInThemedMenu(String name) {
        newCourse = new ArrayList<>();

        for (Course c : menu) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            if (c.getType().equalsIgnoreCase(UsefulStrings.THEMED_COURSE) && c.getDishesArraylist().contains(name)) {
                workloads.removeIf(n -> n.getName().equalsIgnoreCase(c.getName()));

                for (String s : c.getDishesArraylist()) {
                    if (!s.equalsIgnoreCase(name))
                        courseDishes.add(dishesMap.get(s));
                }

                if(!courseDishes.isEmpty()) {
                    Fraction f = menuWorkload(courseDishes);
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    workloads.add(new WorkloadOfTheDay(c.getName(), "menu", f));
                }
            }
            else {
                for (String s : c.getDishesArraylist()) {
                    courseDishes.add(dishesMap.get(s));
                }
                newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
            }
        }

        try {
            coursesWritingTask(newCourse);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù dei generi alimentari (extra) sulla base della richiesta del gestore,
     * in questo caso di aggiungere un genere alimentare extra sulla base delle informazioni fornite dall'utente.
     *
     * @param name nome del genere alimentare extra da aggiungere.
     * @param consumption consumo pro capite.
     */
    public void insertAppetizer(String name, double consumption) {
        newAppetizer = new ArrayList<>();

        appetizers.forEach(a -> newAppetizer.add(new Starter(a.getGenre(), Double.parseDouble(a.getQuantity()))));
        newAppetizer.add(new Starter(name, consumption));

        try {
            appetizersWritingTask(newAppetizer);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù dei generi alimentari (extra) sulla base della richiesta del gestore,
     * in questo caso di rimuovere un genere alimentare extra sulla base delle informazioni fornite dall'utente.
     *
     * @param name nome del genere alimentare extra da rimuovere.
     */
    public void removeAppetizer(String name) {
        newAppetizer = new ArrayList<>();

        appetizers.remove(appetizersMap.get(name));
        appetizersMap.remove(name);
        appetizers.forEach(a -> newAppetizer.add(new Starter(a.getGenre(), Double.parseDouble(a.getQuantity()))));

        try {
            appetizersWritingTask(newAppetizer);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il bevande sulla base della richiesta del gestore,
     * in questo caso di aggiungere un bevanda sulla base delle informazioni fornite dall'utente.
     *
     * @param name nome della bevanda da aggiungere.
     * @param consumption consumo pro capite.
     */
    public void insertDrink(String name, double consumption) {
        newDrinksMenu = new ArrayList<>();

        drinks.forEach(d -> newDrinksMenu.add(new DrinksMenu(d.getName(), Double.parseDouble(d.getQuantity()))));
        newDrinksMenu.add(new DrinksMenu(name, consumption));

        try {
            drinksWritingTask(newDrinksMenu);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che permette di aggiornare il menù delle bevande sulla base della richiesta del gestore,
     * in questo caso di rimuovere una bevanda sulla base delle informazioni fornite dall'utente.
     *
     * @param name nome della bevanda da rimuovere.
     */
    public void removeDrink(String name) {
        newDrinksMenu = new ArrayList<>();

        drinks.remove(drinksMap.get(name));
        drinksMap.remove(name);
        drinks.forEach(d -> newDrinksMenu.add(new DrinksMenu(d.getName(), Double.parseDouble(d.getQuantity()))));

        try {
            drinksWritingTask(newDrinksMenu);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che controlla che il carico di lavoro del menù tematico creato sia consono con quello per persona impostato dal gestore.
     *
     * @param menuWorkload il carico del menù tematico da creare.
     * @return se il menù è creabile o meno, in base al carico di lavoro.
     */
    public boolean checkMenuWorkload(@NotNull Fraction menuWorkload) {
        return menuWorkload.less((4 / 3) * workloadPerPerson);
    }

    /**
     * Metodo che calcola che il carico di lavoro del menù tematico che si vuole creare.
     *
     * @param dishes i piatti che sono stati aggiunti al menù.
     * @return la frazione del carico di lavoro del menù da creare.
     */
    public Fraction newMenuWorkload(@NotNull ArrayList<String> dishes) {
        ArrayList<Dish> temp = new ArrayList<>();
        dishes.forEach(d -> temp.add(dishesMap.get(d)));

        return menuWorkload(temp);
    }

    /**
     * Metodo che verifica che il carico di lavoro del menù tematico esistente e tuttora valido sia coerente col
     * carico di lavoro per persona inizializzato nella nuova giornata lavorativa.
     *
     * @param dishes i piatti che sono stati aggiunti al menù.
     * @return la frazione del carico di lavoro del menù da creare.
     */
    public Fraction menuWorkload(@NotNull ArrayList<Dish> dishes) {
        Fraction f = dishes.get(0).getWorkloadFraction(); // prima frazione, quella del primo piatto.
        if(dishes.size() == 1) {
            // bisogna considerare che il menù potrebbe essere degenere ed avere 1 solo piatto.
            return f;
        } else {
            for(int i = 1; i < dishes.size(); i++)
                f.add(dishes.get(i).getWorkloadFraction());
        }
        return f;
    }

    public void setCookbook(@NotNull ArrayList<CookbookRecipe> cookbookRecipes) {
        this.cookbookRecipes = cookbookRecipes;
        recipeMap = new HashMap<>();
        cookbookRecipes.forEach(e -> recipeMap.put(e.getName(), e));
    }

    public void setMenu(@NotNull ArrayList<Course> menu) {
        this.menu = menu;
        coursesMap = new HashMap<>();
        menu.forEach(m -> coursesMap.put(m.getName(), m));
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

    public boolean dishRecipeMatch(@NotNull Dish d) {
        return recipeMap.containsKey(d.getName());
    }

    public void setFormatter(DateTimeFormatter formatter) {this.formatter = formatter;}

    public void setTomorrow(LocalDate tomorrow) {this.tomorrow = tomorrow;}

    public void setTomorrowString(String tomorrowString) {this.tomorrowString = tomorrowString;}

    public String retriveRecipefromDish(@NotNull Dish d) {
        return recipeMap.get(d.getName()).getIngredientsToString();
    }

    /**
     * Metodo che controlla se un piatto è presente o meno in un menù,
     * tematico e/o alla carta.
     *
     * @param name nome del piatto che si vuole verificare.
     * @return se il piatto è presente o meno nel menu.
     */
    public boolean checkDishInMenu(String name) {
        for (Course c : menu) {
            for (String s : c.getDishesArraylist()) {
                if (s.equalsIgnoreCase(name))
                    return true;
            }
        }
        return false;
    }

    /**
     * Parsing del file per consentire la visione del ricettario al gestore.
     */
    public ArrayList<CookbookRecipe> cookbookParsingTask() throws XMLStreamException {
        XMLParser cookbookParser = new XMLParser(UsefulStrings.COOKBOOK_FILE);
        return new ArrayList<>(cookbookParser.parseXML(CookbookRecipe.class));
    }

    /**
     * Parsing del file per consentire la visione del menù al gestore.
     */
    public ArrayList<Course> coursesParsingTask() throws XMLStreamException {
        XMLParser coursesParser = new XMLParser(UsefulStrings.COURSES_FILE);
        return new ArrayList<>(coursesParser.parseXML(Course.class));
    }

    /**
     * Parsing del file per consentire la visione delle bevande al gestore.
     */
    public ArrayList<Drink> drinksParsingTask() throws XMLStreamException {
        XMLParser drinksParser = new XMLParser(UsefulStrings.DRINKS_FILE);
        return new ArrayList<>(drinksParser.parseXML(Drink.class));
    }

    /**
     * Parsing del file per consentire la visione dei generi alimentari extra al gestore.
     */
    public ArrayList<Appetizer> appetizersParsingTask() throws XMLStreamException {
        XMLParser appetizersParser = new XMLParser(UsefulStrings.APPETIZERS_FILE);
        return new ArrayList<>(appetizersParser.parseXML(Appetizer.class));
    }

    /**
     * Parsing del file per consentire al gestore di inserire i piatti del ricettario nei menu.
     */
    public ArrayList<Dish> dishesParsingTask() throws XMLStreamException {
        XMLParser dishsParser = new XMLParser(UsefulStrings.DISHES_FILE);
        return new ArrayList<>(dishsParser.parseXML(Dish.class));
    }

    /**
     * Writing del ricettario per aggiornare l'XML con la nuova ricetta.
     *
     * @param recipes le ricette da scrivere, compresa quella appena aggiunta.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void cookbookWritingTask(ArrayList<Recipe> recipes) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.COOKBOOK_FILE);
        writer.writeArrayListXML(recipes, UsefulStrings.RECIPES_OUTER_TAG);

        setCookbook(cookbookParsingTask());

        System.out.println(UsefulStrings.COOKBOOK_UPDATED);
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Writing del menu per aggiornare l'XML con il nuovo menu tematico
     * oppure l'aggiornamento del menu tematico.
     *
     * @param updatedCourses i menu da scrivere, compresa quello appena aggiunto.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void coursesWritingTask(ArrayList<Carte> updatedCourses) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.COURSES_FILE);
        writer.writeArrayListXML(updatedCourses, UsefulStrings.COURSE_OUTER_TAG);

        setMenu(coursesParsingTask());

        System.out.println(UsefulStrings.COURSES_UPDATED);
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Writing delle bevande per aggiornare l'XML con la nuova bevanda.
     *
     * @param updatedDrinks le bevande da scrivere, compreso quella appena aggiunta.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void drinksWritingTask(ArrayList<DrinksMenu> updatedDrinks) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.DRINKS_FILE);
        writer.writeArrayListXML(updatedDrinks, UsefulStrings.DRINKS_OUTER_TAG);

        setDrinks(drinksParsingTask());

        System.out.println(UsefulStrings.DRINKS_UPDATED);
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Writing dei generi alimentari extra per aggiornare l'XML con
     * il nuovo genere alimentare extra.
     *
     * @param updatedAppetizers generi alimentari extra da scrivere, compreso quello appena aggiunto.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void appetizersWritingTask(ArrayList<Starter> updatedAppetizers) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.APPETIZERS_FILE);
        writer.writeArrayListXML(updatedAppetizers, UsefulStrings.APPETIZERS_OUTER_TAG);

        setAppetizers(appetizersParsingTask());

        System.out.println(UsefulStrings.APPETIZERS_UPDATED);
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }

    /**
     * Writing dei piatti per aggiornare l'XML con il nuovo piatto.
     *
     * @param updatedAppetizers piatti da scrivere, compreso quello appena aggiunto.
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void dishesWritingTask(ArrayList<NewPlate> updatedAppetizers) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.DISHES_FILE);
        writer.writeArrayListXML(updatedAppetizers, UsefulStrings.DISHES_OUTER_TAG);

        setDishes(dishesParsingTask());
    }

    /**
     * Writing dei carichi di lavoro del giorno.
     *
     * @throws XMLStreamException nel caso in cui il parsing lanci eccezioni, causa errori nel formato, nome del file…
     */
    public void workloadWritingTask() throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.WORKLOADS_FILE);
        writer.writeArrayListXML(workloads, UsefulStrings.WORKLOAD_OUTER_TAG);
    }

    /**
     * Metodo che ritorna il carico di lavoro per persona.
     * @return il carico di lavoro per persona.
     */
    public int getWorkloadPerPerson() {return workloadPerPerson;}

    /**
     * Metodo per settare il carico di lavoro per persona.
     * @param workloadPerPerson il carico di lavoro per persona.
     */
    public void setWorkloadPerPerson(int workloadPerPerson) {this.workloadPerPerson = workloadPerPerson;}

    /**
     * Metodo che ritorna i coperti.
     * @return i coperti.
     */
    public int getCovered() {return covered;}

    /**
     * Metodo per settare i coperti.
     * @param covered  i coperti.
     */
    public void setCovered(int covered) {this.covered = covered;}

    /**
     * Metodo che ritorna se il gestore ha lavorato o meno.
     * @return true se ha lavorato, false altrimenti.
     */
    public boolean getDidIWork() {return didIWork;}

    /**
     * Metodo per settare se il gestore ha lavorato o meno.
     * @param didIWork variabile per indicare se il gestore ha lavorato o meno.
     */
    public void setDidIWork(boolean didIWork) {this.didIWork = didIWork;}

    /**
     * Metodo che ritorna le bevande.
     * @return le bevande.
     */
    public ArrayList<Drink> getDrinks() {return drinks;}

    /**
     * Metodo che ritorna i generi alimentari (extra).
     * @return i generi alimentari (extra).
     */
    public ArrayList<Appetizer> getAppetizers() {return appetizers;}

    /**
     * Metodo che ritorna i piatti.
     * @return i piatti.
     */
    public ArrayList<Dish> getDishes() {return dishes;}

    /**
     * Metodo che ritorna i menù.
     * @return i menù.
     */
    public ArrayList<Course> getMenu() {return menu;}

    /**
     * Metodo che ritorna il ricettario.
     * @return il ricettario.
     */
    public ArrayList<CookbookRecipe> getCookbook() {return cookbookRecipes;}

    /**
     * Metodo che ritorna l'HashMap delle bevande.
     * @return l'HashMap delle bevande.
     */
    public HashMap<String, Drink> getDrinksMap() {return drinksMap;}

    /**
     * Metodo che ritorna l'HashMap dei generi alimentari (extra).
     * @return l'HashMap dei generi alimentari (extra).
     */
    public HashMap<String, Appetizer> getAppetizersMap() {return appetizersMap;}

    /**
     * Metodo che ritorna l'HashMap delle ricette.
     * @return l'HashMap delle ricette.
     */
    public HashMap<String, CookbookRecipe> getRecipeMap() {
        return recipeMap;
    }

    /**
     * Metodo che ritorna l'HashMap dei menù.
     * @return l'HashMap dei menù.
     */
    public HashMap<String, Course> getCoursesMap() {return coursesMap;}

    /**
     * Metodo che ritorna l'HashMap dei piatti.
     * @return l'HashMap dei piatti.
     */
    public HashMap<String, Dish> getDishesMap() {return dishesMap;}
}