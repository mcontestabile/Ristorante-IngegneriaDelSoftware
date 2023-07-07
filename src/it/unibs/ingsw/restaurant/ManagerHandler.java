package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.*;
import it.unibs.ingsw.users.manager.ManagerController;
import it.unibs.ingsw.users.registered_users.UserController;

import java.util.ArrayList;

public class ManagerHandler {
    private ManagerController controller;

    public ManagerHandler(ManagerController controller) {
        this.controller = controller;
    }

    public void helloManager() {
        MenuItem[] items = new MenuItem[]{
                new MenuItem(UsefulStrings.INITIALISE_RESTAURANT_STATUS, () -> updateRestaurant()), // Inizializza i dati di configurazione.
                new MenuItem(UsefulStrings.SEE_RESTAURANT_STATUS, () -> restaurantStatus()), // Visualizza i dati di configurazione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo atto alla visualizzazione delle informazioni del ristorante
     * settate per il giorno lavorativo successivo.
     */
    public void restaurantStatus() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if (controller.getCookbook() == null || controller.getMenu() == null || controller.getAppetizers() == null || controller.getDrinks() == null)
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else {
            System.out.println();
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_STATUS);

            // carico di lavoro per persona.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.slowPrint(UsefulStrings.WORKLOAD_PER_PERSON);
            System.out.println(controller.getWorkloadPerPerson());

            // numero di posti a sedere disponibili nel ristorante.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.slowPrint(UsefulStrings.COVERED);
            System.out.println(controller.getCovered());

            // insieme delle bevande e consumo pro capite.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.printBeverages(controller.getDrinks());

            // insieme dei generi (alimentari) extra e consumo pro capite.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printAppetizers(controller.getAppetizers());

            // corrispondenze piatto-ricetta.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printMatch(controller, controller.getDishes());

            // denominazione e periodo di validità di ciascun piatto.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printDishAvailability(controller.getDishes());
            System.out.println();

            seeCookbook(); // Visualizza ricettario.
            seeMenu(); // Visualizza menu.
        }
    }

    /**
     * Metodo per l'inizializzazione del ristorante.
     */
    public void updateRestaurant(){
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(controller.getUser().isCanIWork()) {
            // Non ha ancora inizializzato il ristorante quel giorno, quindi può farlo.
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP);

            // inizializzazione carico di lavoro per persona.
            setWorkloadPerPerson();

            // recupero informazioni precedentemente valide nel menù del ristorante.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            controller.setRestaurant();
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_RETRIVE_INFOS);
            controller.checkRestaurantDishesAndCourses();
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_RETRIVE_INFOS_COMPLETED);
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);

            AsciiArt.printALaCarteMenu(controller.getMenu());
            AsciiArt.printThemedMenu(controller.getMenu());
            AsciiArt.printAppetizers(controller.getAppetizers());
            AsciiArt.printBeverages(controller.getDrinks());
            AsciiArt.printCookbook(controller.getCookbook());

            Time.pause(Time.MEDIUM_MILLIS_PAUSE);

            // possibilità di modificare le informazioni.
            AsciiArt.slowPrint(UsefulStrings.CHANGE_SOMETHING);
            boolean change = DataInput.yesOrNo(" ");
            if (change) {
                MenuItem[] items = new MenuItem[]{
                        new MenuItem(UsefulStrings.CHANGE_COOKBOOK, () -> setupCookbook()), // Modifica ricetta.
                        new MenuItem(UsefulStrings.CHANGE_COURSES, () -> setupCourses()), // Modifica menu.
                        new MenuItem(UsefulStrings.CHANGE_APPETIZERS, () -> setupAppetizers()), // Modifica genere alimentare extra.
                        new MenuItem(UsefulStrings.CHANGE_DRINKS, () -> setupDrinks()) // Modifica bevanda.
                };

                Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
                menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
                menu.run();
            }

            // salvataggio carico di lavoro dei vari menù.
            controller.writeWorkload();

            // inizializzazione coperti.
            setCovered();

            // salvataggio carico di lavoro sostenibile dal ristorante.
            restaurantWorkload();

            System.out.println();

            // Da qui in poi, non sarà più consentito inizializzare il ristorante per la corrente giornata lavorativa.
            controller.updateUserTurn();
        } else {
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP_NOT_ALLOWED); // l'inizializzazione è gia avvenuta -> STOP, si può fare solo una volta.
        }
    }


    /**
     * Interazione gestore-programma per inserire una ricetta.
     */
    public void insertRecipe() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.RECIPE_MENU);

        AsciiArt.printCookbook(controller.getCookbook());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.RECIPE_NAME);
        } while(controller.getRecipeMap().containsKey(name)); // controllo, per evitare di aggiungere una ricetta con lo stesso nome.

        // inserimento periodo di validità.
        String availability;
        do {
            availability = DataInput.readNotEmptyString(UsefulStrings.RECIPE_VALIDITY);
        } while (!RestaurantDates.checkDate(availability)); // controllo, per evitare di aggiungere una ricetta che abbia una validità nel formato scorretto.

        // inserimento ingredienti.
        System.out.println();
        int nIngredients = DataInput.readPositiveInt(UsefulStrings.RECIPE_INGREDIENTS_NUMBER);
        ArrayList<String> ingredients = new ArrayList<>();
        for (int i = 0; i < nIngredients; i++) {
            String ingredientName = DataInput.readNotEmptyString(UsefulStrings.RECIPE_INGREDIENTS_NAME);
            int ingredientQuantity = DataInput.readPositiveInt(UsefulStrings.RECIPE_INGREDIENTS_WHEIGHT);
            String ingredientUnit = DataInput.optionInput(UsefulStrings.RECIPE_INGREDIENTS_UNIT);
            String ingredient = ingredientQuantity + ingredientUnit + " " + ingredientName;
            ingredients.add(ingredient);
        }

        // inserimento numero porzioni preparabili.
        System.out.println();
        int portions = DataInput.readPositiveInt(UsefulStrings.RECIPE_PORTIONS);

        // inserimento carico di lavoro per porzione.
        System.out.println();
        System.out.println(UsefulStrings.WORKLOAD_PER_PORTION);
        int numerator = DataInput.readPositiveInt(UsefulStrings.NUMERATOR);
        int denominator = DataInput.readPositiveInt(UsefulStrings.DENOMINATOR);
        Fraction workloadPerPortion = new Fraction(numerator, denominator);

        // inserimento nel ricettario e nei piatti disponibili all'inserimento nel menù.
        controller.insertRecipe(name, ingredients, availability, portions, workloadPerPortion);
        controller.insertDish(name, availability, workloadPerPortion);
    }

    /**
     * Interazione gestore-programma per aggiungere un menu tematico.
     * Il menu tematico è stabilito dal gestore e costituito da un elenco
     * di piatti destinati a essere ordinati dal cliente tutti insieme per
     * una persona. Può non essere disponibile per tutto l’arco dell’anno.
     * La somma del carico di lavoro di tutti i piatti contenuti, detta
     * carico di lavoro del menu tematico, deve essere minore o uguale ai
     * 4/3 del carico di lavoro per persona.
     * Possono coesistere più menu tematici, anche contemporaneamente validi.
     */
    public void insertCourse() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.COURSE_MENU);

        // stampa delle informazioni finora disponibili circa la composizione dei menù.
        AsciiArt.printThemedMenu(controller.getMenu());
        AsciiArt.printALaCarteMenu(controller.getMenu());

        // possibilitù di inserimento/rimozione piatti dal menù alla carta.
        AsciiArt.slowPrint(UsefulStrings.INSERT_IN_A_LA_CARTE_MENU);
        boolean insertInALaCarteMenu = DataInput.yesOrNo(" ");
        if(insertInALaCarteMenu) {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.INSERT_IN_A_LA_CARTE_MENU_OPTION, () -> insertDish()), // Togli piatto.
                    new MenuItem(UsefulStrings.REMOVE_IN_A_LA_CARTE_MENU_OPTION, () -> removeDish()), // Aggiungi piatto.
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.COURSE_NAME);
        } while(controller.getCoursesMap().containsKey(name)); // controllo, per evitare di aggiungere un menù con lo stesso nome.

        // inserimento periodo di validità.
        System.out.println();
        String availability;
        do {
            availability = DataInput.readNotEmptyString(UsefulStrings.COURSE_VALIDITY);
        } while (!RestaurantDates.checkDate(availability)); // controllo, per evitare di aggiungere un menù che abbia una validità nel formato scorretto.

        // scelta di quanti piatti compongono il nuovo menù.
        System.out.println();
        int howManyDishes = DataInput.readPositiveInt(UsefulStrings.COURSES_DISHES_NUMBER);

        // se non si sono aggiunti piatti, stampare i piatti che sono a disposizione nel ricettario.
        if(!insertInALaCarteMenu)
            AsciiArt.printDishAvailability(controller.getDishes());

        // inserimento piatti nel nuovo menù.
        ArrayList<String> dishes = new ArrayList<>();
        String dish;
        for (int i = 0; i < howManyDishes; i++) {
            do {
                dish = DataInput.readNotEmptyString(UsefulStrings.COURSES_DISHES);
            } while (!controller.checkDishInMenu(dish) || !RestaurantDates.checkPeriod(controller.getDishesMap().get(dish).getAvailability(), availability, RestaurantDates.workingDayString, RestaurantDates.workingDay)); // il piatto deve, chiaramente, esistere nel ricettario.
            dishes.add(dish);
        }

        // inserimento carico di lavoro del menù.
        Fraction menuWorkload = controller.newMenuWorkload(dishes);
        boolean isMenuWorkloadCorrect = controller.checkMenuWorkload(menuWorkload); // controllo carico di lavoro, se non è corretto, il menù non sarà creato.

        System.out.println();

        // stampa a video in base al risultato di isMenuWorkloadCorrect.
        if(isMenuWorkloadCorrect) {
            controller.insertCourse(name, dishes, availability, menuWorkload);
        } else
            AsciiArt.slowPrint(UsefulStrings.INCORRECT_COURSE_WORKLOAD + menuWorkload + " > " + controller.getWorkloadPerPerson());
    }

    /**
     * Interazione per scegliere il piatto da aggiungere al menù alla carta.
     */
    public void insertDish() {
        System.out.println(UsefulStrings.AVAILABLE_DISHES);
        AsciiArt.printDishAvailability(controller.getDishes());

        String newDish;
        do {
            newDish = DataInput.readNotEmptyString(UsefulStrings.DISH_NAME);
        } while ((!controller.getDishesMap().containsKey(newDish) || controller.checkDishInMenu(newDish)) ||
                !RestaurantDates.checkDate(controller.getDishesMap().get(newDish).getAvailability())); // string deve trovare nei piatti, ma non nel menù alla carta.

        controller.insertDishInALaCarteCourse(newDish);
    }

    /**
     * Interazione gestore-programma per aggiungere un genere alimentare extra.
     */
    public void insertAppetizer() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.APPETIZER_MENU);

        AsciiArt.printAppetizers(controller.getAppetizers());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.APPETIZER_NAME);
        } while(controller.getCoursesMap().containsKey(name)); // controllo, per evitare di aggiungere un genere alimentare extra con lo stesso nome.

        // inserimento consumo pro capite (in hg, quindi formato double).
        System.out.println();
        double consumptionPerPerson = DataInput.readPositiveDouble(UsefulStrings.APPETIZER_CONSUMPTION);

        controller.insertAppetizer(name, consumptionPerPerson);
    }

    /**
     * Interazione gestore-programma per aggiungere una bevanda.
     */
    public void insertDrink() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.DRINK_MENU);

        AsciiArt.printBeverages(controller.getDrinks());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.DRINK_NAME);
        } while (controller.getDrinksMap().containsKey(name)); // controllo, per evitare di aggiungere un genere alimentare extra con lo stesso nome.

        // inserimento consumo pro capite (in L, quindi formato double).
        System.out.println();
        double consumptionPerPerson = DataInput.readPositiveDouble(UsefulStrings.DRINKS_CONSUMPTION);

        controller.insertDrink(name, consumptionPerPerson);
    }

    /**
     * Interazione gestore-programma per rimuovere una ricetta.
     */
    public void removeRecipe() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.RECIPE_REMOVER);

        AsciiArt.printCookbook(controller.getCookbook());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.RECIPE_NAME);
        } while (!controller.getRecipeMap().containsKey(name)); // controllo, per evitare di rimuovere una ricetta che poi non riconosce.

        /*
         * Controllo se il piatto si trova in qualche menu, tematico o meno.
         * In caso affermativo, va rimosso dal menu (alla carta e/o tematico)
         * in questione, perché se la ricetta non c'è più => non può
         * esserci il suo piatto, causa corrispondenza piatto-ricetta, ma se
         * non c'è il piatto non può esistere nel menu!
         * In caso contrario, basta toglierlo solo dai piatti e dal ricettario.
         */
        if (controller.checkDishInMenu(name)) {
            /*
             * C'è stata corrispondenza fra name e un piatto nel menù, chiediamo al gestore se è
             * sicuro di voler rimuovere il piatto dal menù attualmente in vigore in Ristorante.
             */
            AsciiArt.slowPrint(UsefulStrings.RECIPE_REMOVER_WARNING);
            boolean remove = DataInput.yesOrNo("");

            if (remove) {
                /*
                 * Va aggiornato tutto: ricettario, piatti, per via della corrispondenza
                 * col ricettario, e il menù, sia alla carta che tematico, vista la possibilità
                 * che il piatto si trovi solo in quello alla carta oppure in entrambi.
                 */
                controller.removeDishInALaCarteCourse(name);
                controller.removeDishInThemedMenu(name);
                controller.removeRecipe(name);
                controller.removeDish(name);
            }
        } else {
            /*
             * Il piatto è presente solo nel ricettario e nei piatti, aventi corrispondenza
             * col ricettario, quindi si vanno ad aggiornare le informazioni solo lì.
             */
            controller.removeRecipe(name);
            controller.removeDish(name);
        }
    }

    /**
     * Interazione gestore-programma per rimuovere un menu tematico.
     */
    public void removeCourse() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.COURSE_REMOVER);

        AsciiArt.printThemedMenu(controller.getMenu());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.COURSE_NAME);
        } while (!controller.getCoursesMap().containsKey(name)); // controllo, per evitare di rimuovere un menu tematico che poi non riconosce.

        controller.removeCourse(name);
    }

    public void removeDish() {
        System.out.println(UsefulStrings.AVAILABLE_DISHES);
        AsciiArt.printDishAvailability(controller.getDishes());
        String removeDish;
        do {
            removeDish = DataInput.readNotEmptyString(UsefulStrings.DISH_NAME);
        } while (!controller.getDishesMap().containsKey(removeDish) && !controller.checkDishInMenu(removeDish)); // string deve trovare nei piatti e nel menù alla carta.

        controller.removeDishInALaCarteCourse(removeDish);
        controller.removeDishInThemedMenu(removeDish); // potrebbe trovarsi anche in un menù tematico!
    }

    /**
     * Interazione gestore-programma per rimuovere un genere alimentare extra.
     */
    public void removeAppetizer() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.APPETIZER_REMOVER);

        AsciiArt.printAppetizers(controller.getAppetizers());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.APPETIZER_NAME);
        } while (!controller.getAppetizersMap().containsKey(name)); // controllo, per evitare di rimuovere un genere alimentare exyta che poi non riconosce.

        controller.removeAppetizer(name);
    }

    /**
     * Interazione gestore-programma per rimuovere una bevanda.
     */
    public void removeDrink() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.DRINK_REMOVER);

        AsciiArt.printBeverages(controller.getDrinks());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.DRINK_NAME);
        } while (!controller.getDrinksMap().containsKey(name)); // controllo, per evitare di rimuovere un genere alimentare exyta che poi non riconosce.

        controller.removeDrink(name);
    }

    /**
     * Interazione per settare il carico di lavoro per persona.
     */
    public void setWorkloadPerPerson() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.WORKLOAD_PER_PERSON_SETUP);
        int workloadPerPerson = DataInput.readPositiveInt(UsefulStrings.INSERT);
        controller.setWorkloadPerPerson(workloadPerPerson);
    }

    /**
     * Interazione per settare il numero di coperti del giorno.
     */
    public void setCovered() {
        AsciiArt.slowPrint(UsefulStrings.SET_COVERED);
        controller.setCovered(DataInput.readPositiveInt("» "));
    }

    /**
     * Carico di lavoro sostenibile dal ristorante (a ogni pasto).
     * Ammonta al prodotto del carico di lavoro per persona per il
     * numero complessivo di posti a sedere del ristorante accresciuto del 20%.
     * @return il carico di lavoro del ristorante.
     */
    public double restaurantWorkload() {
        double a = controller.getWorkloadPerPerson() * controller.getCovered();
        double b = 20/100;
        double c = controller.getWorkloadPerPerson() * controller.getCovered();

        double w = (a * b) + c;
        controller.setRestaurantWorkload(w);

        return w;
    }

    /**
     * Metodo atto alla visualizzazione del menù attualmente in vigore nel ristorante.
     */
    public void seeMenu() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.SEE_MENU_CHOICE);

        if(controller.getMenu() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printThemedMenu(controller.getMenu());
    }

    /**
     * Metodo atto alla visualizzazione del ricettario attualmente in vigore nel ristorante.
     */
    public void seeCookbook() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(controller.getCookbook() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printCookbook(controller.getCookbook());
    }

    /**
     * Metodo per la gestione del ricettario del ristorante.
     */
    public void setupCookbook() {
        // Menu principale del ricettario, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_RECIPE, () -> insertRecipe()), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_RECIPE, () -> removeRecipe()), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione dei menu del ristorante.
     */
    public void setupCourses(){
        // Menu principale dei menù del giorno, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_COURSE, () -> insertCourse()), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_COURSE, () -> removeCourse()), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione dei generi alimentari extra del ristorante.
     */
    public void setupAppetizers(){
        // Menu principale dei generi alimentari (extra), permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_APPETIZER, () -> insertAppetizer()), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_APPETIZER, () -> removeAppetizer()), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione delle bevande del ristorante.
     */
    public void setupDrinks(){
        // Menu principale delle bevande, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_DRINK, () -> insertDrink()), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_DRINK, () -> removeDrink()), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }
}
