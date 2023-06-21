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
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ManagerController extends UserController {
    private Manager manager;

    /**
     * Costruttore del controller.
     * @param userQueue la lista degli utenti con cui interagisce.
     * @param manager l'utente specifico di questo controller.
     */
    public ManagerController(Queue<User> userQueue, Manager manager) {
        super(userQueue);
        this.manager = manager;
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
        List<Carte> newCourse = new ArrayList<>();

        // Itero in ciascun menù esistente.
        for(Course c : manager.getMenu()) {
            // Se la data di validità del menù è coerente, bisogna controllare all'interno del menù se i suoi piatti lo sono.
            if (RestaurantDates.checkDate(c.getValidation())) {
                ArrayList<Dish> courseDishes = new ArrayList<>();

                for (String s : c.getDishesArraylist()) {
                    if (RestaurantDates.checkDate(manager.getDishesMap().get(s).getAvailability()))
                        courseDishes.add(manager.getDishesMap().get(s));
                    else continue;
                }

                Fraction f = menuWorkload(courseDishes);
                // Controlliamo che il menu tematico controllato abbia un carico di lavoro ammissibile in base al carico di lavoro di persona del giorno.
                if(c.getType().equalsIgnoreCase(UsefulStrings.THEMED_COURSE) && checkMenuWorkload(f)) {
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    manager.getWorkloads().add(new WorkloadOfTheDay(c.getName(), "menu", f));
                }
                else if(c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE)) {
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    for (Dish d : courseDishes)
                        manager.getWorkloads().add(new WorkloadOfTheDay(d.getName(), "piatto", d.getWorkloadFraction()));
                }
            } else continue;
        }

        try {
            manager.writingTask((ArrayList<Carte>) newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Recipe> recipes = new ArrayList<>();

        for (CookbookRecipe r : manager.getCookbookRecipes()) {
            Fraction workload = new Fraction(r.getNumerator(), r.getDenominator());
            recipes.add(new Recipe(r.getName(), manager.getDishesMap().get(r.getName()).getAvailability(), r.getPortion(), workload, r.getIngredients()));
        }

        recipes.add(new Recipe(name, availability, Integer.toString(portions), workloadPerPortion, ingredients));

        try {
            manager.writingTask(recipes, UsefulStrings.COOKBOOK_FILE, UsefulStrings.RECIPES_OUTER_TAG);
            manager.setCookbook(manager.parsingTask(UsefulStrings.COOKBOOK_FILE, CookbookRecipe.class));
            System.out.println(UsefulStrings.COOKBOOK_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<NewPlate> newDish = new ArrayList<>();

        for (Dish d : manager.getDishes())
            newDish.add(new NewPlate(d.getName(), manager.getDishesMap().get(d.getName()).getAvailability(), d.getWorkloadFraction()));

        newDish.add(new NewPlate(name, availability, workloadPerPerson));

        try {
            manager.writingTask(newDish, UsefulStrings.DISHES_FILE, UsefulStrings.DISHES_OUTER_TAG);
            manager.setDishes(manager.parsingTask(UsefulStrings.DISHES_FILE, Dish.class));
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
        ArrayList<Recipe> recipes = new ArrayList<>();

        manager.getCookbookRecipes().remove(manager.getRecipeMap().get(name));
        manager.getRecipeMap().remove(name);

        manager.getCookbookRecipes().forEach(r -> recipes.add(new Recipe(r.getName(), manager.getDishesMap().get(r.getName()).getAvailability(), r.getPortion(), new Fraction(r.getNumerator(), r.getDenominator()), r.getIngredients())));

        try {
            manager.writingTask(recipes, UsefulStrings.COOKBOOK_FILE, UsefulStrings.RECIPES_OUTER_TAG);
            manager.setCookbook(manager.parsingTask(UsefulStrings.COOKBOOK_FILE, CookbookRecipe.class));
            System.out.println(UsefulStrings.COOKBOOK_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<NewPlate> newDish = new ArrayList<>();

        manager.getDishes().remove(manager.getDishesMap().get(name));
        manager.getDishesMap().remove(name);

        manager.getDishes().forEach(d -> newDish.add(new NewPlate(d.getName(), d.getAvailability(), d.getWorkloadFraction())));

        try {
            manager.writingTask(newDish, UsefulStrings.DISHES_FILE, UsefulStrings.DISHES_OUTER_TAG);
            manager.setDishes(manager.parsingTask(UsefulStrings.DISHES_FILE, Dish.class));
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
        ArrayList<Carte> newCourse = new ArrayList<>();

        for (Course c : manager.getMenu()) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist())
                courseDishes.add(manager.getDishesMap().get(s));

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        ArrayList<Dish> newMenuDishes = new ArrayList<>();
        dishes.forEach(d -> newMenuDishes.add(manager.getDishesMap().get(d)));

        newCourse.add(new Carte(name, UsefulStrings.THEMED_COURSE, availability, newMenuDishes));

        manager.getWorkloads().add(new WorkloadOfTheDay(name, "menu", menuWorkload));

        try {
            manager.writingTask(newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Carte> newCourse = new ArrayList<>();

        manager.getMenu().remove(manager.getCoursesMap().get(name));
        manager.getCoursesMap().remove(name);

        manager.getWorkloads().removeIf(n -> n.getName().equals(name));

        for (Course c : manager.getMenu()) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist()) {
                courseDishes.add(manager.getDishesMap().get(s));
            }

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        try {
            manager.writingTask(newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Carte> newCourse = new ArrayList<>();

        /*
         * Scopo del codice: prendere e aggiungere il piatto al menu alla carta.
         * Come fare? Ciclo su tutti i menù disponibili con un for, controllo che
         * tipo di menu è, distinguendo le casistiche:
         *
         *         c menu alla carta -> prendo tutte i suoi piatti + aggiungo quello nuovo.
         *           c menu tematico -> è come prima.
         */
        for (Course c : manager.getMenu()) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist())
                courseDishes.add(manager.getDishesMap().get(s));

            // controllo il tipo di menù, il piatto va aggiunto SOLO al menù alla carta.
            if (c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE))
                courseDishes.add(manager.getDishesMap().get(name)); // aggiungo il nome del piatto che si vuole.

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        manager.getWorkloads().add(new WorkloadOfTheDay(name, "piatto", manager.getDishesMap().get(name).getWorkloadFraction()));

        try {
            manager.writingTask(newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Carte> newCourse = new ArrayList<>();

        manager.getWorkloads().removeIf(n -> n.getName().equalsIgnoreCase(name));

        // ATTENZIONE: il piatto potrebbe essere in un menù tematico! Il for deve ciclare anche in quelli!
        for (Course c : manager.getMenu()) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            for (String s : c.getDishesArraylist()) {
                if (!c.getType().equalsIgnoreCase(UsefulStrings.A_LA_CARTE_COURSE) || !s.equalsIgnoreCase(name))
                    courseDishes.add(manager.getDishesMap().get(s));
            }

            newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
        }

        try {
            manager.writingTask(newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Carte> newCourse = new ArrayList<>();

        for (Course c : manager.getMenu()) {
            ArrayList<Dish> courseDishes = new ArrayList<>();

            if (c.getType().equalsIgnoreCase(UsefulStrings.THEMED_COURSE) && c.getDishesArraylist().contains(name)) {
                manager.getWorkloads().removeIf(n -> n.getName().equalsIgnoreCase(c.getName()));

                for (String s : c.getDishesArraylist()) {
                    if (!s.equalsIgnoreCase(name))
                        courseDishes.add(manager.getDishesMap().get(s));
                }

                if(!courseDishes.isEmpty()) {
                    Fraction f = menuWorkload(courseDishes);
                    newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
                    manager.getWorkloads().add(new WorkloadOfTheDay(c.getName(), "menu", f));
                }
            }
            else {
                for (String s : c.getDishesArraylist()) {
                    courseDishes.add(manager.getDishesMap().get(s));
                }
                newCourse.add(new Carte(c.getName(), c.getType(), c.getValidation(), courseDishes));
            }
        }

        try {
            manager.writingTask(newCourse, UsefulStrings.COURSES_FILE, UsefulStrings.COURSE_OUTER_TAG);
            manager.setMenu(manager.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
            System.out.println(UsefulStrings.COURSES_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Starter> newAppetizer = new ArrayList<>();

        manager.getAppetizers().forEach(a -> newAppetizer.add(new Starter(a.getGenre(), Double.parseDouble(a.getQuantity()))));
        newAppetizer.add(new Starter(name, consumption));

        try {
            manager.writingTask(newAppetizer, UsefulStrings.APPETIZERS_FILE, UsefulStrings.APPETIZERS_OUTER_TAG);
            manager.setAppetizers(manager.parsingTask(UsefulStrings.APPETIZERS_FILE, Appetizer.class));
            System.out.println(UsefulStrings.APPETIZERS_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<Starter> newAppetizer = new ArrayList<>();

        manager.getAppetizers().remove(manager.getAppetizersMap().get(name));
        manager.getAppetizersMap().remove(name);
        manager.getAppetizers().forEach(a -> newAppetizer.add(new Starter(a.getGenre(), Double.parseDouble(a.getQuantity()))));

        try {
            manager.writingTask(newAppetizer, UsefulStrings.APPETIZERS_FILE, UsefulStrings.APPETIZERS_OUTER_TAG);
            manager.setAppetizers(manager.parsingTask(UsefulStrings.APPETIZERS_FILE, Appetizer.class));
            System.out.println(UsefulStrings.APPETIZERS_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<DrinksMenu> newDrinksMenu = new ArrayList<>();

        manager.getDrinks().forEach(d -> newDrinksMenu.add(new DrinksMenu(d.getName(), Double.parseDouble(d.getQuantity()))));
        newDrinksMenu.add(new DrinksMenu(name, consumption));

        try {
            manager.writingTask(newDrinksMenu, UsefulStrings.DRINKS_FILE, UsefulStrings.DRINKS_OUTER_TAG);
            manager.setDrinks(manager.parsingTask(UsefulStrings.DRINKS_FILE, Drink.class));
            System.out.println(UsefulStrings.DRINKS_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        ArrayList<DrinksMenu> newDrinksMenu = new ArrayList<>();

        manager.getDrinks().remove(manager.getDrinksMap().get(name));
        manager.getDrinksMap().remove(name);
        manager.getDrinks().forEach(d -> newDrinksMenu.add(new DrinksMenu(d.getName(), Double.parseDouble(d.getQuantity()))));

        try {
            manager.writingTask(newDrinksMenu, UsefulStrings.DRINKS_FILE, UsefulStrings.DRINKS_OUTER_TAG);
            manager.setDrinks(manager.parsingTask(UsefulStrings.DRINKS_FILE, Drink.class));
            System.out.println(UsefulStrings.DRINKS_UPDATED);
            DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
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
        return menuWorkload.less((4 / 3) * manager.getWorkloadPerPerson());
    }

    /**
     * Metodo che calcola che il carico di lavoro del menù tematico che si vuole creare.
     *
     * @param dishes i piatti che sono stati aggiunti al menù.
     * @return la frazione del carico di lavoro del menù da creare.
     */
    public Fraction newMenuWorkload(@NotNull ArrayList<String> dishes) {
        ArrayList<Dish> temp = new ArrayList<>();
        dishes.forEach(d -> temp.add(manager.getDishesMap().get(d)));

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


    public boolean dishRecipeMatch(@NotNull Dish d) {
        return manager.getRecipeMap().containsKey(d.getName());
    }

    public String retriveRecipefromDish(@NotNull Dish d) {return manager.getRecipeMap().get(d.getName()).getIngredientsToString();}

    /**
     * Metodo che controlla se un piatto è presente o meno in un menù,
     * tematico e/o alla carta.
     *
     * @param name nome del piatto che si vuole verificare.
     * @return se il piatto è presente o meno nel menu.
     */
    public boolean checkDishInMenu(String name) {
        for (Course c : manager.getMenu()) {
            for (String s : c.getDishesArraylist()) {
                if (s.equalsIgnoreCase(name))
                    return true;
            }
        }
        return false;
    }

    public void writeWorkload() {
        try {
            manager.writingTask((ArrayList)manager.getWorkloads(), UsefulStrings.WORKLOADS_FILE, UsefulStrings.WORKLOAD_OUTER_TAG);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public Manager getUser() {return manager;}

    public List<Course> getMenu() {return manager.getMenu();}

    public List<Appetizer> getAppetizers() {return manager.getAppetizers();}

    public List<Drink> getDrinks() {return manager.getDrinks();}

    public List<CookbookRecipe> getCookbook() {return manager.getCookbook();}

    public Map<String, CookbookRecipe> getRecipeMap() {return manager.getRecipeMap();}

    public List<Dish> getDishes() {return manager.getDishes();}

    public Map<String,Dish> getDishesMap() {return manager.getDishesMap();}

    public Map<String, Course> getCoursesMap() {return manager.getCoursesMap();}

    public Map<String, Drink> getDrinksMap() {return manager.getDrinksMap();}

    public Map<String, Appetizer> getAppetizersMap() {return manager.getAppetizersMap();}

    public void setWorkloadPerPerson(int workloadPerPerson) {manager.setWorkloadPerPerson(workloadPerPerson);}
}
