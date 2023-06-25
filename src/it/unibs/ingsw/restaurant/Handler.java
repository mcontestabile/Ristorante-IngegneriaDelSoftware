package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.*;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.manager.*;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.registered_users.UserDAO;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgentController;
import it.unibs.ingsw.users.warehouse_worker.Article;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

/**
 * Questa classe è quella che permette un dialogo con gli utenti,
 * i quali si occupano del funzionamento ottimale del ristorante.
 */
public class Handler {

    /**
     * Merce da acquistare per il giorno lavorativo successivo.
     */
    ArrayList<Article> shoppingList;

    /**
     * Questo metodo lancia il messaggio di benvenuto una volta
     * avviato il programma {@code Magazzino}.
     */
    public void welcomeMessage() {
        System.out.println(AsciiArt.coloredText(UsefulStrings.WELCOME, AsciiArt.color.rainbowSeq));
        /*
         * Impostiamo il ristorante in modo tale che le prenotazioni siano pervenute un giorno
         * lavorativo prima della data di ricevimento dei clienti. Il ristorante, quindi, si può
         * inizializzare solo dal lunedì al venerdì, quindi OGGI non deve essere un festivo.
         */
        if(!RestaurantDates.isHoliday(RestaurantDates.today)) {
            RestaurantDates.setWorkingDay();

            System.out.println(AsciiArt.coloredText(UsefulStrings.DAY + " " + RestaurantDates.workingDayString, AsciiArt.color.rainbowSeq));

            UserController controller = new UserController(UserDAO.configureUsers());

            startMenu(controller);
        } else {
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED3);
        }
    }

    /**
     * Questo è il main menu, che permette di avviare l'invio di ordini
     * al ristorante o di terminare il programma {@code Ristorante}.
     */
    public void startMenu(UserController controller) {
        /*
         * "Addormento" il thread per permettere all'utente di
         * visualizzare il messaggio di benvenuto e avere uno stacco,
         * visivamente più elegante, fra la selezione delle opzioni
         * e il benvenuto del primo menu.
         */
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        // Menu principale, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.FIRST_FIRST_MENU_OPTION, () -> loginTask(controller)),
                new MenuItem(UsefulStrings.SECOND_FIRST_MENU_OPTION, this::authorTask)
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.END_MENU_OPTION);
        menu.run();

        AsciiArt.slowPrint("\n" + UsefulStrings.GOODBYE);
    }

    /**
     * Nel caso l'utente abbia selezionato l'opzione {@code «Autenticazione utente.»}, il programma
     * avvia questo metodo. Questo è un sotto-menu, qui si arriva a ciò che il gestore
     * può fare nel {@code Ristorante}. Qui è presente il menu le scelte fra le attività che
     * il gestore fa per amministrare il Ristorante.
     */
    public void loginTask(UserController controller) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        AsciiArt.slowPrint(UsefulStrings.USERS_SIGNIN);
        String username = DataInput.readNotEmptyString("username » ");
        String password = DataInput.readNotEmptyString("password » ");

        LoginController loginController = new LoginController(controller);
        User user = loginController.authenticateUser(username, password);

        if (user != null) {
            AsciiArt.slowPrint(UsefulStrings.WELCOME_USER + user.getUsername());
            switch (controller.findUserCategory(user)) {
                case "gestore" -> managerTask((Manager) user, controller);
                case "addetto alle prenotazioni" -> reservationsAgentTask((ReservationsAgent) user, controller);
                case "magazziniere" -> wareHouseWorkerTask((WarehouseWorker) user, controller);
            }
        } else
            System.out.println(UsefulStrings.ACCESS_DENIED);
    }

    /**
     * Metodo rappresentativo dell'interazione col gestore, il quale
     * inizializza e visualizza i dati di configurazione relativi al
     * ristorante, crea e visualizza il ricettario e i menu.
     */
    public void managerTask(Manager user, UserController controller) {
        ManagerController mController = new ManagerController(controller.getQueue(), user);

        // sotto-menu del gestore.
        MenuItem[] items = new MenuItem[]{
                new MenuItem(UsefulStrings.INITIALISE_RESTAURANT_STATUS, () -> updateRestaurant(mController)), // Inizializza i dati di configurazione.
                new MenuItem(UsefulStrings.SEE_RESTAURANT_STATUS, () -> restaurantStatus(mController)), // Visualizza i dati di configurazione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Interazione gestore-programma per inserire una ricetta.
     */
    public void insertRecipe(ManagerController controller) {
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
    public void insertCourse(ManagerController controller) {
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
                    new MenuItem(UsefulStrings.INSERT_IN_A_LA_CARTE_MENU_OPTION, () -> insertDish(controller)), // Togli piatto.
                    new MenuItem(UsefulStrings.REMOVE_IN_A_LA_CARTE_MENU_OPTION, () -> removeDish(controller)), // Aggiungi piatto.
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
    public void insertDish(ManagerController controller) {
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
    public void insertAppetizer(ManagerController controller) {
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
    public void insertDrink(ManagerController controller) {
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
    public void removeRecipe(ManagerController controller) {
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
    public void removeCourse(ManagerController controller) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.COURSE_REMOVER);

        AsciiArt.printThemedMenu(controller.getMenu());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.COURSE_NAME);
        } while (!controller.getCoursesMap().containsKey(name)); // controllo, per evitare di rimuovere un menu tematico che poi non riconosce.

        controller.removeCourse(name);
    }

    public void removeDish(ManagerController controller) {
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
    public void removeAppetizer(ManagerController controller) {
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
    public void removeDrink(ManagerController controller) {
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
    public void setWorkloadPerPerson(ManagerController controller) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.WORKLOAD_PER_PERSON_SETUP);
        int workloadPerPerson = DataInput.readPositiveInt(UsefulStrings.INSERT);
        controller.setWorkloadPerPerson(workloadPerPerson);
    }

    /**
     * Interazione per settare il numero di coperti del giorno.
     */
    public void setCovered(ManagerController controller) {
        AsciiArt.slowPrint(UsefulStrings.SET_COVERED);
        controller.setCovered(DataInput.readPositiveInt("» "));
    }

    /**
     * Carico di lavoro sostenibile dal ristorante (a ogni pasto).
     * Ammonta al prodotto del carico di lavoro per persona per il
     * numero complessivo di posti a sedere del ristorante accresciuto del 20%.
     * @return il carico di lavoro del ristorante.
     */
    public double restaurantWorkload(UserController controller) {
        double a = controller.getWorkloadPerPerson() * controller.getCovered();
        double b = 20/100;
        double c = controller.getWorkloadPerPerson() * controller.getCovered();

        double w = (a * b) + c;
        controller.setRestaurantWorkload(w);

        return w;
    }

    /**
     * Metodo atto alla visualizzazione del menù attualmente in vigore nel ristorante.
     * @param controller l'oggetto che si occupa di fare da tramite con manager, che possiede il menù.
     */
    public void seeMenu(ManagerController controller) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.SEE_MENU_CHOICE);

        if(controller.getMenu() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printThemedMenu(controller.getMenu());
    }

    /**
     * Metodo atto alla visualizzazione del ricettario attualmente in vigore nel ristorante.
     * @param controller l'oggetto che si occupa di fare da tramite con manager, che possiede il menù.
     */
    public void seeCookbook(ManagerController controller) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(controller.getCookbook() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printCookbook(controller.getCookbook());
    }

    /**
     * Metodo atto alla visualizzazione delle informazioni del ristorante
     * settate per il giorno lavorativo successivo.
     */
    public void restaurantStatus(ManagerController controller) {
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

            seeCookbook(controller); // Visualizza ricettario.
            seeMenu(controller); // Visualizza menu.
        }
    }

    /**
     * Metodo per l'inizializzazione del ristorante.
     */
    public void updateRestaurant(ManagerController controller){
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(controller.getUser().isCanIWork()) {
            // Non ha ancora inizializzato il ristorante quel giorno, quindi può farlo.
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP);

            // inizializzazione carico di lavoro per persona.
            setWorkloadPerPerson(controller);

            // recupero informazioni precedentemente valide nel menù del ristorante.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
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
                        new MenuItem(UsefulStrings.CHANGE_COOKBOOK, () -> setupCookbook(controller)), // Modifica ricetta.
                        new MenuItem(UsefulStrings.CHANGE_COURSES, () -> setupCourses(controller)), // Modifica menu.
                        new MenuItem(UsefulStrings.CHANGE_APPETIZERS, () -> setupAppetizers(controller)), // Modifica genere alimentare extra.
                        new MenuItem(UsefulStrings.CHANGE_DRINKS, () -> setupDrinks(controller)) // Modifica bevanda.
                };

                Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
                menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
                menu.run();
            }

            // salvataggio carico di lavoro dei vari menù.
            controller.writeWorkload();

            // inizializzazione coperti.
            setCovered(controller);

            // salvataggio carico di lavoro sostenibile dal ristorante.
            restaurantWorkload(controller);

            System.out.println();

            // Da qui in poi, non sarà più consentito inizializzare il ristorante per la corrente giornata lavorativa.
            controller.updateUserTurn();
        } else {
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP_NOT_ALLOWED); // l'inizializzazione è gia avvenuta -> STOP, si può fare solo una volta.
        }
    }

    /**
     * Metodo per la gestione del ricettario del ristorante.
     */
    public void setupCookbook(ManagerController controller) {
        // Menu principale del ricettario, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_RECIPE, () -> insertRecipe(controller)), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_RECIPE, () -> removeRecipe(controller)), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione dei menu del ristorante.
     */
    public void setupCourses(ManagerController controller){
        // Menu principale dei menù del giorno, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_COURSE, () -> insertCourse(controller)), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_COURSE, () -> removeCourse(controller)), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione dei generi alimentari extra del ristorante.
     */
    public void setupAppetizers(ManagerController controller){
        // Menu principale dei generi alimentari (extra), permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_APPETIZER, () -> insertAppetizer(controller)), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_APPETIZER, () -> removeAppetizer(controller)), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo per la gestione delle bevande del ristorante.
     */
    public void setupDrinks(ManagerController controller){
        // Menu principale delle bevande, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_DRINK, () -> insertDrink(controller)), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_DRINK, () -> removeDrink(controller)), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo rappresentativo dell'interazione con l'addetto, il quale
     * permette l'aggiunta delle prenotazioni e il loro salvataggio sul relativo file.
     */
    public void reservationsAgentTask(ReservationsAgent user, UserController controller) {
        ReservationsAgentController raController = new ReservationsAgentController(controller.getQueue(), user);
        if(!controller.getCanIWork(user)) {
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED4);
        } else {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.UPDATE_AGENDA_MENU_VOICE, () -> {
                        if(user.getCopertiRaggiunti() < raController.getCovered() && raController.getCaricoRaggiunto() < controller.getRestaurantWorkload())
                            updateAgenda(user, raController);
                        else
                            System.out.println(UsefulStrings.NO_MORE_RESERVATION_MESSAGE);
                    }),
                    new MenuItem(UsefulStrings.SAVE_IN_RES_ARCHIVE_MENU_VOICE, () -> saveInResArchive(user))
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }

    }

    /**
     * Metodo per l'aggiornamento dell'agenda riguardante le prenotazioni.
     */
    public void updateAgenda(ReservationsAgent user, ReservationsAgentController controller){
        controller.parseCourses();
        controller.parseWorkloads();

        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.UPDATE_AGENDA);

        String name;
        String menu_piatto;

        int resCover;
        int itemCover = 0;

        // somma dei coperti di un item (non menu) di una prenotazione per la relativa item_list
        int sumItemCover;
        // somma dei coperti di un item (menu) di una prenotazione per la relativa item_list  ->  servirà per controllare che si potrà avere soltanto un menù tematico a testa
        int sumMenuItemCover;

        AsciiArt.printALaCarteMenu(user.getMenu());
        AsciiArt.printThemedMenu(user.getMenu());


        do{

            HashMap<String, String> item_list = new HashMap<>();

            user.seeInfoCovered((int) controller.getCovered());

            name = user.askResName();

            resCover = user.askResCover((int) controller.getCovered());

            sumItemCover = 0;
            sumMenuItemCover = 0;

            do{
                user.seeInfoWorkload(controller.getRestaurantWorkload());

                menu_piatto = user.askMenuPiatto(item_list, controller.getRestaurantWorkload());

                if(!user.isDish(menu_piatto) && (sumMenuItemCover < resCover)){ // se non è un Dish -> è un menù tematico  &&  un menù a testa!

                    do {
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    } while (user.exceedsOneMenuPerPerson(itemCover, sumMenuItemCover, resCover) ||
                            user.exceedsRestaurantWorkload(user.calculateWorkload(menu_piatto, itemCover), user.getCaricoRaggiunto(), controller.getRestaurantWorkload()));

                    sumMenuItemCover += user.addItem(itemCover, sumMenuItemCover, menu_piatto, item_list);

                }else if(user.isDish(menu_piatto)){ // se è un piatto normale, dovrò solo controllare che non si ecceda il carico del ristorante
                    do{
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    }while(user.exceedsRestaurantWorkload(user.calculateWorkload(menu_piatto, itemCover), user.getCaricoRaggiunto(), controller.getRestaurantWorkload()));

                    sumItemCover += user.addItem(itemCover, sumItemCover, menu_piatto, item_list);

                }else{
                    System.out.println(UsefulStrings.ONE_MENU_PER_PERSON);
                }

                // aggiorno il carico di lavoro
                user.updateCaricoRaggiunto(user.calculateWorkload(menu_piatto, itemCover));

            }while(user.moreItems(sumItemCover, sumMenuItemCover, resCover) && user.workloadRestaurantNotExceeded(controller.getRestaurantWorkload()));

            user.insertReservation(name, Integer.toString(resCover), item_list);

            // aggiorno i coperti
            user.updateCopertiRaggiunti(resCover);

        }while((DataInput.yesOrNo(UsefulStrings.QUE_ADD_ANOTHER_RESERVATION) &&
                user.restaurantNotFull((int) controller.getCovered())) &&
                user.workloadRestaurantNotExceeded(controller.getRestaurantWorkload()));

        // salvataggio nell'archivio prenotazioni
        saveInResArchive(user);

        // ora che l'agenda è stata scritta, il magazziniere potrà creare la lista della spesa a seconda delle prenotazioni raccolte
        controller.updateUserTurn();
    }

    /**
     * Metodo per quanto riguarda il salvataggio nell'archivio delle prenotazioni.
     */
    public void saveInResArchive(ReservationsAgent user) {
        boolean save = DataInput.yesOrNo(UsefulStrings.QUE_SAVE_IN_RES_ARCHIVE);
        if (save) {
            try {
                user.salvaInArchivioPrenotazioni(RestaurantDates.workingDay.format(RestaurantDates.formatter));
            } catch (IOException e) {
                System.out.println(UsefulStrings.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * @throws
     */
    public void wareHouseWorkerTask(WarehouseWorker user, UserController controller) {
        if (!controller.getCanIWork(user)) {
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED5);
        } else {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem("Visualizza lo stato del magazzino.", () -> {
                        try {
                            printWareHouse(user);
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Lista della spesa.", () -> {
                        try {
                            createShoppingList(user);
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Porta ingrediente in cucina.", () -> {
                        try {
                            getIngredientFromWareHouse(user);
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Riporta ingrediente in magazzino.", () -> {
                        try {
                            putIngredientInWareHouse(user);
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Scarta prodotto.", () -> {
                        try {
                            trashIngredientFromWareHouse(user);
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    })
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void printWareHouse(WarehouseWorker user) throws XMLStreamException {
        if(!user.getWareHouseArticles().isEmpty())
            AsciiArt.printWareHouse(user.getWareHouseArticles());
        else
            System.out.println("Il magazzino è vuoto.");

        if(!user.getKitchenList().isEmpty())
            AsciiArt.printKitchen(user.getKitchenList());
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void createShoppingList(WarehouseWorker user) throws XMLStreamException{
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        user.readReservations();
        shoppingList = user.createShoppingList();

        if(!shoppingList.isEmpty()) {
            AsciiArt.printShoppingList(shoppingList);
            if(DataInput.yesOrNo("Procedere all'acquisto? ")) {
                user.buyShoppingList(shoppingList);
                System.out.println("Acquistati correttamente "+ shoppingList.size() + " articoli.");
            }
        } else {
            System.out.println("Hai già tutti gli ingredienti occorrenti per le prenotazioni attuali.");
        }

    }

    /**
     *
     * @throws XMLStreamException
     */
    public void getIngredientFromWareHouse(WarehouseWorker user) throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Prelievo ingredienti \n");
        if(!user.getWareHouseArticles().isEmpty()) {


            AsciiArt.printWareHouse(user.getWareHouseArticles());

            do {
                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (user.getArticle(name) != null) {
                    Article article = user.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzone: max " + article.getQuantity() + article.getMeasure() + "! \n");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (user.removeArticle(name, qty, true)) {
                        System.out.println("Prelevati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (user.getArticle(name).getQuantity() == 0)
                            System.out.println("Attenzione: hai finito le scorte di " + name + "!\n");
                    } else {
                        System.out.println("\nNome ingrediente errato");
                    }
                } else {
                    System.out.println("\nNon ho trovato nessun ingrediente chiamato \"" + name + "\" !");
                }
            } while (DataInput.yesOrNo("Vuoi prelevare qualcos'altro? "));
        } else {
            System.out.println("\nMagazzino vuoto!\n");
        }
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void putIngredientInWareHouse(WarehouseWorker user) throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Reinserimento ingredienti in magazzino\n");
        if(!user.getKitchenList().isEmpty()) {


            String name;
            double qty;
            //stampa degli ingredienti in cucina
            AsciiArt.printKitchen(user.getKitchenList());

            do {
                do {
                    name = DataInput.readNotEmptyString("nome ingrediente » ");
                } while (user.getKitchenMap().get(name) == null);
                do {
                    qty = DataInput.readDoubleWithMinimum("quantità ingrediente (max: " + user.getKitchenMap().get(name).getQuantity() + ") »", 0);
                } while (qty > user.getKitchenMap().get(name).getQuantity());

                if (user.insertArticle(name, qty, user.getKitchenMap().get(name).getMeasure())) {
                    user.getKitchenMap().get(name).decrementQuantity(qty);
                    if(user.getKitchenMap().get(name).getQuantity() == 0) user.getKitchenMap().remove(name);
                    System.out.println("\nInseriti correttamente " + qty + user.getKitchenMap().get(name).getMeasure() + " di " + name + ".\n");
                }
            } while (DataInput.yesOrNo("Vuoi inserire qualcos'altro? "));
        } else {
            System.out.println("\nNessun ingrediente è attualmente in uso!");
        }
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void trashIngredientFromWareHouse(WarehouseWorker user) throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Scarta ingrediente dal magazzino \n");
        if(!user.getWareHouseArticles().isEmpty()) {

            do {

                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (user.getArticle(name) != null) {
                    Article article = user.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzione: max " + article.getQuantity() + article.getMeasure() + "!\n ");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (user.removeArticle(name, qty, false)) {
                        System.out.println("\nScartati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (user.getArticle(name).getQuantity() == 0)
                            System.out.println("Attenzione hai finito le scorte di " + name + "!\n");
                    } else {
                        System.out.println("\nNome ingrediente errato!");
                    }
                } else {
                    System.out.println("\nNon ho trovato nessun ingrediente chiamato \"" + name + "\" !");
                }
            } while (DataInput.yesOrNo("Vuoi scartare qualcos'altro? "));
        } else {
            System.out.println("\nMagazzino vuoto!\n");
        }
    }

    /**
     * Questo metodo mostra chi ha scritto il codice di {@code Ristorante}.
     */
    public final void authorTask() {
        System.out.println(UsefulStrings.AUTHOR);
        Time.pause(Time.LOW_MILLIS_PAUSE);
        DataInput.readString(UsefulStrings.ENTER_TO_CONTINUE);
    }
}