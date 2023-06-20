package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.WorkloadOfTheDay;
import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.*;
import it.unibs.ingsw.users.User;
import it.unibs.ingsw.users.manager.*;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.registered_users.UserCredentials;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.warehouse_worker.Article;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Questa classe è quella che permette un dialogo con gli utenti,
 * i quali si occupano del funzionamento ottimale del ristorante.
 */
public class Handler {
    /**
     * Formatter della data, serve per avere la conversione nel formato italiano.
     */
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /**
     * Data del giorno attuale.
     */
    LocalDate today = LocalDate.now();
    /**
     * Data del giorno lavorativo successivo.
     */
    LocalDate workingDay;
    /**
     * Data del giorno lavorativo successivo formattata.
     */
    String workingDayString;

    /**
     * Utenti autorizzati ad accedere a Ristorante, ossia gestore, addetto alle prenotazioni e magazziniere.
     */
    ArrayList<UserCredentials> users;
    /**
     * Versione HashMap della collezione di utenti, utile per ciclare in o(1), invece di o(n).
     */
    HashMap<String, UserCredentials> usersMap;

    /**
     * Merce da acquistare per il giorno lavorativo successivo.
     */
    ArrayList<Article> shoppingList;

    /**
     * Gestore.
     */
    Manager manager;
    /**
     * Addetto alle vendite.
     */
    ReservationsAgent agent;
    /**
     * Magazziniere.
     */
    WarehouseWorker warehouseWorker;

    /**
     * Carico di lavoro del ristorante.
     */
    double restaurantWorkload = 0;


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
        if(!RestaurantDates.isHoliday(today)) {
            /*
             * Una volta confermato che il programma è stato avviato in un giorno in cui il
             * ristorante è operativo, è il momento di controllare se l'indomani è un festivo.
             * Se oggi è venerdì/prefestivo, si setta il ristorante per IL GIORNO LAVORATIVO SUCCESSIVO.
             * Quindi, bisogna controllare che giorno è e impostare workingDay di conseguenza.
             */
            if(!RestaurantDates.isHoliday(today.plusDays(1))) {
                // Se non è un festivo, si può settare senza problemi workingDay.
                workingDay = today.plusDays(1);
            } else {
                /*
                 * Se è un festivo, dalla data dobbiamo partire dal presupposto di aggiungere almeno
                 * due giorni alla data odierna, per andare a dopodomani, che, potenzialmente, non
                 * dovrebbe essere un festivo. Sarà il metodo a controllare che giorno lavorativo servirà.
                 */
                workingDay = RestaurantDates.setWorkingDay(today.plusDays(2));
            }

            workingDayString = workingDay.format(formatter);

            System.out.println(AsciiArt.coloredText(UsefulStrings.DAY + " " + workingDayString, AsciiArt.color.rainbowSeq));

            UserController controller = new UserController();
            controller.configureUsers();

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
                new MenuItem(UsefulStrings.FIRST_FIRST_MENU_OPTION, () -> {
                    loginTask(controller);
                }),
                new MenuItem(UsefulStrings.SECOND_FIRST_MENU_OPTION, this::authorTask),
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
                case "gestore" -> {
                    try {
                        managerTask(user, controller);
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                }
                case "addetto alle prenotazioni" -> reservationsAgentTask(user, controller);
                case "magazziniere" -> wareHouseWorkerTask(user, controller);
            }
        } else
            System.out.println(UsefulStrings.ACCESS_DENIED);
    }

    /**
     * Metodo rappresentativo dell'interazione col gestore, il quale
     * inizializza e visualizza i dati di configurazione relativi al
     * ristorante, crea e visualizza il ricettario e i menu.
     * @throws XMLStreamException nel caso in cui lo stream dei dati lanci errori.
     */
    public void managerTask(User user, UserController controller) throws XMLStreamException {
        // sotto-menu del gestore.
        MenuItem[] items = new MenuItem[]{
                new MenuItem(UsefulStrings.INITIALISE_RESTAURANT_STATUS, () -> {
                    try {
                        updateRestaurant();
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                }), // Inizializza i dati di configurazione.
                new MenuItem(UsefulStrings.SEE_RESTAURANT_STATUS, this::restaurantStatus), // Visualizza i dati di configurazione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Interazione gestore-programma per inserire una ricetta.
     */
    public void insertRecipe() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.RECIPE_MENU);

        AsciiArt.printCookbook(manager.getCookbook());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.RECIPE_NAME);
        } while(manager.getRecipeMap().containsKey(name)); // controllo, per evitare di aggiungere una ricetta con lo stesso nome.

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
        manager.insertRecipe(name, ingredients, availability, portions, workloadPerPortion);
        manager.insertDish(name, availability, workloadPerPortion);
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
        AsciiArt.printThemedMenu(manager.getMenu());
        AsciiArt.printALaCarteMenu(manager.getMenu());

        // possibilitù di inserimento/rimozione piatti dal menù alla carta.
        AsciiArt.slowPrint(UsefulStrings.INSERT_IN_A_LA_CARTE_MENU);
        boolean insertInALaCarteMenu = DataInput.yesOrNo(" ");
        if(insertInALaCarteMenu) {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.INSERT_IN_A_LA_CARTE_MENU_OPTION, this::insertDish), // Togli piatto.
                    new MenuItem(UsefulStrings.REMOVE_IN_A_LA_CARTE_MENU_OPTION, this::removeDish), // Aggiungi piatto.
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.COURSE_NAME);
        } while(manager.getCoursesMap().containsKey(name)); // controllo, per evitare di aggiungere un menù con lo stesso nome.

        // inserimento periodo di validità.
        System.out.println();
        String availability;
        do {
            availability = DataInput.readNotEmptyString(UsefulStrings.COURSE_VALIDITY);
        } while (!RestaurantDates.checkDate(availability, workingDayString, workingDay)); // controllo, per evitare di aggiungere un menù che abbia una validità nel formato scorretto.

        // scelta di quanti piatti compongono il nuovo menù.
        System.out.println();
        int howManyDishes = DataInput.readPositiveInt(UsefulStrings.COURSES_DISHES_NUMBER);

        // se non si sono aggiunti piatti, stampare i piatti che sono a disposizione nel ricettario.
        if(!insertInALaCarteMenu)
            AsciiArt.printDishAvailability(manager.getDishes());

        // inserimento piatti nel nuovo menù.
        ArrayList<String> dishes = new ArrayList<>();
        String dish;
        for (int i = 0; i < howManyDishes; i++) {
            do {
                dish = DataInput.readNotEmptyString(UsefulStrings.COURSES_DISHES);
            } while (!manager.checkDishInMenu(dish) || !RestaurantDates.checkPeriod(manager.getDishesMap().get(dish).getAvailability(), availability, workingDayString, workingDay)); // il piatto deve, chiaramente, esistere nel ricettario.
            dishes.add(dish);
        }

        // inserimento carico di lavoro del menù.
        Fraction menuWorkload = manager.newMenuWorkload(dishes);
        boolean isMenuWorkloadCorrect = manager.checkMenuWorkload(menuWorkload); // controllo carico di lavoro, se non è corretto, il menù non sarà creato.

        System.out.println();

        // stampa a video in base al risultato di isMenuWorkloadCorrect.
        if(isMenuWorkloadCorrect) {
            manager.insertCourse(name, dishes, availability, menuWorkload);
        } else
            AsciiArt.slowPrint(UsefulStrings.INCORRECT_COURSE_WORKLOAD + menuWorkload + " > " + manager.getWorkloadPerPerson());
    }

    /**
     * Interazione per scegliere il piatto da aggiungere al menù alla carta.
     */
    public void insertDish() {
        System.out.println(UsefulStrings.AVAILABLE_DISHES);
        AsciiArt.printDishAvailability(manager.getDishes());

        String newDish;
        do {
            newDish = DataInput.readNotEmptyString(UsefulStrings.DISH_NAME);
        } while ((!manager.getDishesMap().containsKey(newDish) || manager.checkDishInMenu(newDish)) ||
                !RestaurantDates.checkDate(manager.getDishesMap().get(newDish).getAvailability(), workingDayString, workingDay)); // string deve trovare nei piatti, ma non nel menù alla carta.

        manager.insertDishInALaCarteCourse(newDish);
    }

    /**
     * Interazione gestore-programma per aggiungere un genere alimentare extra.
     */
    public void insertAppetizer() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.APPETIZER_MENU);

        AsciiArt.printAppetizers(manager.getAppetizers());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.APPETIZER_NAME);
        } while(manager.getCoursesMap().containsKey(name)); // controllo, per evitare di aggiungere un genere alimentare extra con lo stesso nome.

        // inserimento consumo pro capite (in hg, quindi formato double).
        System.out.println();
        double consumptionPerPerson = DataInput.readPositiveDouble(UsefulStrings.APPETIZER_CONSUMPTION);

        manager.insertAppetizer(name, consumptionPerPerson);
    }

    /**
     * Interazione gestore-programma per aggiungere una bevanda.
     */
    public void insertDrink() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.DRINK_MENU);

        AsciiArt.printBeverages(manager.getDrinks());

        // inserimento nome.
        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.DRINK_NAME);
        } while (manager.getDrinksMap().containsKey(name)); // controllo, per evitare di aggiungere un genere alimentare extra con lo stesso nome.

        // inserimento consumo pro capite (in L, quindi formato double).
        System.out.println();
        double consumptionPerPerson = DataInput.readPositiveDouble(UsefulStrings.DRINKS_CONSUMPTION);

        manager.insertDrink(name, consumptionPerPerson);
    }

    /**
     * Interazione gestore-programma per rimuovere una ricetta.
     */
    public void removeRecipe() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.RECIPE_REMOVER);

        AsciiArt.printCookbook(manager.getCookbook());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.RECIPE_NAME);
        } while (!manager.getRecipeMap().containsKey(name)); // controllo, per evitare di rimuovere una ricetta che poi non riconosce.

        /*
         * Controllo se il piatto si trova in qualche menu, tematico o meno.
         * In caso affermativo, va rimosso dal menu (alla carta e/o tematico)
         * in questione, perché se la ricetta non c'è più => non può
         * esserci il suo piatto, causa corrispondenza piatto-ricetta, ma se
         * non c'è il piatto non può esistere nel menu!
         * In caso contrario, basta toglierlo solo dai piatti e dal ricettario.
         */
        if (manager.checkDishInMenu(name)) {
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
                manager.removeDishInALaCarteCourse(name);
                manager.removeDishInThemedMenu(name);
                manager.removeRecipe(name);
                manager.removeDish(name);
            }
        } else {
            /*
             * Il piatto è presente solo nel ricettario e nei piatti, aventi corrispondenza
             * col ricettario, quindi si vanno ad aggiornare le informazioni solo lì.
             */
            manager.removeRecipe(name);
            manager.removeDish(name);
        }
    }

    /**
     * Interazione gestore-programma per rimuovere un menu tematico.
     */
    public void removeCourse() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.COURSE_REMOVER);

        AsciiArt.printThemedMenu(manager.getMenu());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.COURSE_NAME);
        } while (!manager.getCoursesMap().containsKey(name)); // controllo, per evitare di rimuovere un menu tematico che poi non riconosce.

        manager.removeCourse(name);
    }

    public void removeDish() {
        System.out.println(UsefulStrings.AVAILABLE_DISHES);
        AsciiArt.printDishAvailability(manager.getDishes());
        String removeDish;
        do {
            removeDish = DataInput.readNotEmptyString(UsefulStrings.DISH_NAME);
        } while (!manager.getDishesMap().containsKey(removeDish) && !manager.checkDishInMenu(removeDish)); // string deve trovare nei piatti e nel menù alla carta.

        manager.removeDishInALaCarteCourse(removeDish);
        manager.removeDishInThemedMenu(removeDish); // potrebbe trovarsi anche in un menù tematico!
    }

    /**
     * Interazione gestore-programma per rimuovere un genere alimentare extra.
     */
    public void removeAppetizer() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.APPETIZER_REMOVER);

        AsciiArt.printAppetizers(manager.getAppetizers());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.APPETIZER_NAME);
        } while (!manager.getAppetizersMap().containsKey(name)); // controllo, per evitare di rimuovere un genere alimentare exyta che poi non riconosce.

        manager.removeAppetizer(name);
    }

    /**
     * Interazione gestore-programma per rimuovere una bevanda.
     */
    public void removeDrink() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.DRINK_REMOVER);

        AsciiArt.printBeverages(manager.getDrinks());

        String name;
        do {
            name = DataInput.readNotEmptyString(UsefulStrings.DRINK_NAME);
        } while (!manager.getDrinksMap().containsKey(name)); // controllo, per evitare di rimuovere un genere alimentare exyta che poi non riconosce.

        manager.removeDrink(name);
    }

    /**
     * Interazione per settare il carico di lavoro per persona.
     */
    public void setWorkloadPerPerson() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.WORKLOAD_PER_PERSON_SETUP);
        int workloadPerPerson = DataInput.readPositiveInt(UsefulStrings.INSERT);
        manager.setWorkloadPerPerson(workloadPerPerson);
    }

    /**
     * Interazione per settare il numero di coperti del giorno.
     */
    public void setCovered() {
        AsciiArt.slowPrint(UsefulStrings.SET_COVERED);
        manager.setCovered(DataInput.readPositiveInt("» "));
    }

    /**
     * Carico di lavoro sostenibile dal ristorante (a ogni pasto).
     * Ammonta al prodotto del carico di lavoro per persona per il
     * numero complessivo di posti a sedere del ristorante accresciuto del 20%.
     * @return il carico di lavoro del ristorante.
     */
    public double restaurantWorkload() {
        return (manager.getWorkloadPerPerson() * manager.getCovered()) * 20/100 + manager.getWorkloadPerPerson() * manager.getCovered();
    }

    /**
     * Metodo atto alla visualizzazione del menù attualmente in vigore nel ristorante.
     * @param courses il menù a disposizione dei clienti nel giorno lavorativo successivo.
     */
    public void seeMenu(ArrayList<Course> courses) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        System.out.println(UsefulStrings.SEE_MENU_CHOICE);

        if(manager.getMenu() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printThemedMenu(courses);
    }

    /**
     * Metodo atto alla visualizzazione del ricettario attualmente in vigore nel ristorante.
     * @param cookbook il ricettario a disposizione della cucina il giorno lavorativo successivo.
     */
    public void seeCookbook(ArrayList<CookbookRecipe> cookbook) {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(manager.getCookbook() == null)
            System.out.println(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else
            AsciiArt.printCookbook(cookbook);
    }

    /**
     * Metodo atto alla visualizzazione delle informazioni del ristorante
     * settate per il giorno lavorativo successivo.
     */
    public void restaurantStatus() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if (manager.getCookbook() == null || manager.getMenu() == null || manager.getAppetizers() == null || manager.getDrinks() == null)
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_NOT_INITIALIZED);
        else {
            System.out.println();
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_STATUS);

            // carico di lavoro per persona.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.slowPrint(UsefulStrings.WORKLOAD_PER_PERSON);
            System.out.println(manager.getWorkloadPerPerson());

            // numero di posti a sedere disponibili nel ristorante.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.slowPrint(UsefulStrings.COVERED);
            System.out.println(manager.getCovered());

            // insieme delle bevande e consumo pro capite.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.printBeverages(manager.getDrinks());

            // insieme dei generi (alimentari) extra e consumo pro capite.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printAppetizers(manager.getAppetizers());

            // corrispondenze piatto-ricetta.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printMatch(manager, manager.getDishes());

            // denominazione e periodo di validità di ciascun piatto.
            Time.pause(Time.HIGH_MILLIS_PAUSE);
            AsciiArt.printDishAvailability(manager.getDishes());
            System.out.println();

            seeCookbook(manager.getCookbook()); // Visualizza ricettario.
            seeMenu(manager.getMenu()); // Visualizza menu.
        }
    }

    /**
     * Metodo per l'inizializzazione del ristorante.
     * @throws XMLStreamException nel caso in cui lo stream dei dati lanci una qualche eccezione.
     */
    public void updateRestaurant() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);

        if(manager.isCanIWork()) {
            // Non ha ancora inizializzato il ristorante quel giorno, quindi può farlo.
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP);

            // inizializzazione carico di lavoro per persona.
            setWorkloadPerPerson();

            // recupero informazioni precedentemente valide nel menù del ristorante.
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_RETRIVE_INFOS);
            manager.checkRestaurantDishesAndCourses();
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_RETRIVE_INFOS_COMPLETED);
            Time.pause(Time.MEDIUM_MILLIS_PAUSE);

            AsciiArt.printALaCarteMenu(manager.getMenu());
            AsciiArt.printThemedMenu(manager.getMenu());
            AsciiArt.printAppetizers(manager.getAppetizers());
            AsciiArt.printBeverages(manager.getDrinks());
            AsciiArt.printCookbook(manager.getCookbook());

            Time.pause(Time.MEDIUM_MILLIS_PAUSE);

            // possibilità di modificare le informazioni.
            AsciiArt.slowPrint(UsefulStrings.CHANGE_SOMETHING);
            boolean change = DataInput.yesOrNo(" ");
            if (change) {
                MenuItem[] items = new MenuItem[]{
                        new MenuItem(UsefulStrings.CHANGE_COOKBOOK, this::setupCookbook), // Modifica ricetta.
                        new MenuItem(UsefulStrings.CHANGE_COURSES, this::setupCourses), // Modifica menu.
                        new MenuItem(UsefulStrings.CHANGE_APPETIZERS, this::setupAppetizers), // Modifica genere alimentare extra.
                        new MenuItem(UsefulStrings.CHANGE_DRINKS, this::setupDrinks) // Modifica bevanda.
                };

                Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
                menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
                menu.run();
            }

            // salvataggio carico di lavoro dei vari menù.
            manager.writingTask(manager.getWorkloads(), WorkloadOfTheDay.class, UsefulStrings.WORKLOADS_FILE, UsefulStrings.WORKLOAD_OUTER_TAG);

            // inizializzazione coperti.
            setCovered();

            // salvataggio carico di lavoro sostenibile dal ristorante.
            restaurantWorkload = restaurantWorkload();

            System.out.println();

            manager.setCanIWork(false); // Da qui in poi, non sarà più consentito inizializzare il ristorante per la corrente giornata lavorativa.
            agent.setCanIWork(true);
        } else {
            AsciiArt.slowPrint(UsefulStrings.RESTAURANT_SETUP_NOT_ALLOWED); // l'inizializzazione è gia avvenuta -> STOP, si può fare solo una volta.
        }
    }

    /**
     * Metodo per la gestione del ricettario del ristorante.
     */
    public void setupCookbook() {
        // Menu principale del ricettario, permette l'accesso ai sotto-menu.
        MenuItem[] items  = {
                new MenuItem(UsefulStrings.ADD_RECIPE, this::insertRecipe), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_RECIPE, this::removeRecipe), // rimozione.
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
                new MenuItem(UsefulStrings.ADD_COURSE, this::insertCourse), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_COURSE, this::removeCourse), // rimozione.
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
                new MenuItem(UsefulStrings.ADD_APPETIZER, this::insertAppetizer), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_APPETIZER, this::removeAppetizer), // rimozione.
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
                new MenuItem(UsefulStrings.ADD_DRINK, this::insertDrink), // inserimento.
                new MenuItem(UsefulStrings.REMOVE_DRINK, this::removeDrink), // rimozione.
        };

        Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
        menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
        menu.run();
    }

    /**
     * Metodo rappresentativo dell'interazione con l'addetto, il quale
     * permette l'aggiunta delle prenotazioni e il loro salvataggio sul relativo file.
     */
    public void reservationsAgentTask(User user, UserController controller) {
        if(!controller.getCanIWork(user)) {
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED4);
        } else {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.UPDATE_AGENDA_MENU_VOICE, () -> {
                        if(agent.getCopertiRaggiunti() < manager.getCovered() && agent.getCarico_raggiunto() < restaurantWorkload)
                            try{
                                updateAgenda();
                            }catch (XMLStreamException e){
                                System.out.println(UsefulStrings.ERROR_MESSAGE);
                            }
                        else
                            System.out.println(UsefulStrings.NO_MORE_RESERVATION_MESSAGE);
                    }),
                    new MenuItem(UsefulStrings.SAVE_IN_RES_ARCHIVE_MENU_VOICE, this::saveInResArchive)
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }

    }

    /**
     * Metodo per l'aggiornamento dell'agenda riguardante le prenotazioni.
     */
    public void updateAgenda() throws XMLStreamException{
        agent.setMenu(agent.coursesParsingTask()); // prende dal file i menu con i relativi piatti
        agent.setWorkloads(agent.workloadsParsingTask()); // prende dal file i carichi di lavoro dei menu/piatti relativi la giornata
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

        AsciiArt.printALaCarteMenu(agent.getMenu());
        AsciiArt.printThemedMenu(agent.getMenu());


        do{

            HashMap<String, String> item_list = new HashMap<>();

            agent.seeInfoCovered(manager.getCovered());

            name = agent.askResName();

            resCover = agent.askResCover(manager.getCovered());

            sumItemCover = 0;
            sumMenuItemCover = 0;

            do{
                agent.seeInfoWorkload(restaurantWorkload);

                menu_piatto = agent.askMenuPiatto(item_list, restaurantWorkload);

                if(!agent.isDish(menu_piatto) && (sumMenuItemCover < resCover)){ // se non è un Dish -> è un menù tematico  &&  un menù a testa!

                    do {
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    } while (agent.exceedsOneMenuPerPerson(itemCover, sumMenuItemCover, resCover) ||
                            agent.exceedsRestaurantWorkload(agent.calculateWorkload(menu_piatto, itemCover), agent.getCarico_raggiunto(), restaurantWorkload));

                    sumMenuItemCover += agent.addItem(itemCover, sumMenuItemCover, menu_piatto, item_list);

                }else if(agent.isDish(menu_piatto)){ // se è un piatto normale, dovrò solo controllare che non si ecceda il carico del ristorante
                    do{
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    }while(agent.exceedsRestaurantWorkload(agent.calculateWorkload(menu_piatto, itemCover), agent.getCarico_raggiunto(), restaurantWorkload));

                    sumItemCover += agent.addItem(itemCover, sumItemCover, menu_piatto, item_list);
                }else{
                    System.out.println(UsefulStrings.ONE_MENU_PER_PERSON);
                }

                // aggiorno il carico di lavoro
                agent.updateCaricoRaggiunto(agent.calculateWorkload(menu_piatto, itemCover));

            }while(agent.moreItems(sumItemCover, sumMenuItemCover, resCover) && agent.workloadRestaurantNotExceeded(restaurantWorkload));

            agent.insertReservation(name, Integer.toString(resCover), item_list);

            // aggiorno i coperti
            agent.updateCopertiRaggiunti(resCover);

        }while((DataInput.yesOrNo(UsefulStrings.QUE_ADD_ANOTHER_RESERVATION) && agent.restaurantNotFull(manager.getCovered())) && agent.workloadRestaurantNotExceeded(restaurantWorkload));

        // salvataggio nell'archivio prenotazioni
        saveInResArchive();

        warehouseWorker.setCanIWork(true); // ora che l'agenda è stata scritta, il magazziniere potrà creare la lista della spesa a seconda delle prenotazioni raccolte
    }

    /**
     * Metodo per quanto riguarda il salvataggio nell'archivio delle prenotazioni.
     */
    public void saveInResArchive() {
        boolean save = DataInput.yesOrNo(UsefulStrings.QUE_SAVE_IN_RES_ARCHIVE);
        if (save) {
            try {
                agent.salvaInArchivioPrenotazioni(workingDay.format(formatter));
            } catch (IOException e) {
                System.out.println(UsefulStrings.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * @throws
     */
    public void wareHouseWorkerTask(User user, UserController controller) {
        if (!controller.getCanIWork(user)) {
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED5);
        } else {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem("Visualizza lo stato del magazzino.", () -> {
                        try {
                            printWareHouse();
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Lista della spesa.", () -> {
                        try {
                            createShoppingList();
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Porta ingrediente in cucina.", () -> {
                        try {
                            getIngredientFromWareHouse();
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Riporta ingrediente in magazzino.", () -> {
                        try {
                            putIngredientInWareHouse();
                        } catch (XMLStreamException e) {
                            throw new RuntimeException(e);
                        }
                    }),
                    new MenuItem("Scarta prodotto.", () -> {
                        try {
                            trashIngredientFromWareHouse();
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
    public void printWareHouse() throws XMLStreamException {
        if(!warehouseWorker.getWareHouseArticles().isEmpty())
            AsciiArt.printWareHouse(warehouseWorker.getWareHouseArticles());
        else
            System.out.println("Il magazzino è vuoto.");

        if(!warehouseWorker.getKitchenList().isEmpty())
            AsciiArt.printKitchen(warehouseWorker.getKitchenList());
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void createShoppingList() throws XMLStreamException{
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        warehouseWorker.readReservations();
        shoppingList = warehouseWorker.createShoppingList();

        if(!shoppingList.isEmpty()) {
            AsciiArt.printShoppingList(shoppingList);
            if(DataInput.yesOrNo("Procedere all'acquisto? ")) {
                warehouseWorker.buyShoppingList(shoppingList);
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
    public void getIngredientFromWareHouse() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Prelievo ingredienti \n");
        if(!warehouseWorker.getWareHouseArticles().isEmpty()) {


            AsciiArt.printWareHouse(warehouseWorker.getWareHouseArticles());

            do {
                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (warehouseWorker.getArticle(name) != null) {
                    Article article = warehouseWorker.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzone: max " + article.getQuantity() + article.getMeasure() + "! \n");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (warehouseWorker.removeArticle(name, qty, true)) {
                        System.out.println("Prelevati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (warehouseWorker.getArticle(name).getQuantity() == 0)
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
    public void putIngredientInWareHouse() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Reinserimento ingredienti in magazzino\n");
        if(!warehouseWorker.getKitchenList().isEmpty()) {


            String name;
            double qty;
            //stampa degli ingredienti in cucina
            AsciiArt.printKitchen(warehouseWorker.getKitchenList());

            do {
                do {
                    name = DataInput.readNotEmptyString("nome ingrediente » ");
                } while (warehouseWorker.getKitchenMap().get(name) == null);
                do {
                    qty = DataInput.readDoubleWithMinimum("quantità ingrediente (max: " + warehouseWorker.getKitchenMap().get(name).getQuantity() + ") »", 0);
                } while (qty > warehouseWorker.getKitchenMap().get(name).getQuantity());

                if (warehouseWorker.insertArticle(name, qty, warehouseWorker.getKitchenMap().get(name).getMeasure())) {
                    warehouseWorker.getKitchenMap().get(name).decrementQuantity(qty);
                    if(warehouseWorker.getKitchenMap().get(name).getQuantity() == 0) warehouseWorker.getKitchenMap().remove(name);
                    System.out.println("\nInseriti correttamente " + qty + warehouseWorker.getKitchenMap().get(name).getMeasure() + " di " + name + ".\n");
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
    public void trashIngredientFromWareHouse() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Scarta ingrediente dal magazzino \n");
        if(!warehouseWorker.getWareHouseArticles().isEmpty()) {

            do {

                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (warehouseWorker.getArticle(name) != null) {
                    Article article = warehouseWorker.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzione: max " + article.getQuantity() + article.getMeasure() + "!\n ");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (warehouseWorker.removeArticle(name, qty, false)) {
                        System.out.println("\nScartati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (warehouseWorker.getArticle(name).getQuantity() == 0)
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