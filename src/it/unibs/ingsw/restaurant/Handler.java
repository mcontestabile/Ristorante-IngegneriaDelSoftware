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
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorkerView;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * Questa classe è quella che permette un dialogo con gli utenti,
 * i quali si occupano del funzionamento ottimale del ristorante.
 */
public class Handler {

    /**
     * Merce da acquistare per il giorno lavorativo successivo.
     */
    List<Article> shoppingList;

    WarehouseWorkerView warehouseWorkerView = new WarehouseWorkerView();

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
                case "magazziniere" -> warehouseWorkerView.wareHouseWorkerTask((WarehouseWorker) user, controller);
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
        ManagerHandler handler = new ManagerHandler(new ManagerController(controller.getQueue(), user));
        // sotto-menu del gestore.
        handler.helloManager();
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
                        if(raController.getCopertiRaggiunti() < raController.getCovered() && raController.getCaricoRaggiunto() < raController.getRestaurantWorkload())
                            updateAgenda(raController);
                        else
                            System.out.println(UsefulStrings.NO_MORE_RESERVATION_MESSAGE);
                    }),
                    new MenuItem(UsefulStrings.SAVE_IN_RES_ARCHIVE_MENU_VOICE, () -> saveInArchiveTask(raController))
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
        }

    }

    /**
     * Metodo per l'aggiornamento dell'agenda riguardante le prenotazioni.
     */
    public void updateAgenda(ReservationsAgentController controller){
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

        boolean doYouWantToContinue = true;

        AsciiArt.printALaCarteMenu(controller.getMenu());
        AsciiArt.printThemedMenu(controller.getMenu());


        do{

            HashMap<String, String> item_list = new HashMap<>();


            AsciiArt.seeInfoCovered(controller.getCopertiRaggiunti(), (int)controller.getCovered());
            AsciiArt.seeInfoWorkload(controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());

            do{
                name = DataInput.readNotEmptyString(UsefulStrings.RESERVATION_NAME);
            }while(controller.isRepeated(name, controller.getReservationNameList()));

            do{
                resCover = DataInput.readPositiveInt(UsefulStrings.RES_COVER);
            }while(controller.exceedsCover(resCover, controller.getCopertiRaggiunti(), (int) controller.getCovered()));

            sumItemCover = 0;
            sumMenuItemCover = 0;

            do{
                AsciiArt.seeInfoWorkload(controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());

                do{
                    menu_piatto = DataInput.readNotEmptyString(UsefulStrings.MENU_DISH_NAME);
                }while(!controller.isInMenu(menu_piatto) ||
                        controller.isRepeated(menu_piatto, item_list.keySet()) ||
                        controller.exceedsRestaurantWorkload(controller.calculateWorkload(menu_piatto, 1), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()));

                if(!controller.isDish(menu_piatto) && (sumMenuItemCover < resCover)){ // se non è un Dish -> è un menù tematico  &&  un menù a testa!

                    do {
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    } while (controller.exceedsOneMenuPerPerson(itemCover, sumMenuItemCover, resCover) ||
                            controller.exceedsRestaurantWorkload(controller.calculateWorkload(menu_piatto, itemCover), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()));

                    sumMenuItemCover += controller.addItem(itemCover, sumMenuItemCover, menu_piatto, item_list);

                }else if(controller.isDish(menu_piatto)){ // se è un piatto normale, dovrò solo controllare che non si ecceda il carico del ristorante
                    do{
                        itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
                    }while(controller.exceedsRestaurantWorkload(controller.calculateWorkload(menu_piatto, itemCover), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()));

                    sumItemCover += controller.addItem(itemCover, sumItemCover, menu_piatto, item_list);

                }else{
                    System.out.println(UsefulStrings.ONE_MENU_PER_PERSON);
                }

                if(!controller.moreItemsNeeded(sumItemCover, sumMenuItemCover, resCover))
                    doYouWantToContinue = DataInput.yesOrNo(UsefulStrings.MORE_ITEMS);


                controller.insertReservation(name, Integer.toString(resCover), item_list);

                controller.updateCopertiRaggiunti(resCover);

                controller.updateCaricoRaggiunto(controller.calculateWorkload(menu_piatto, itemCover));

                controller.writeAgenda();

            }while(doYouWantToContinue && controller.workloadRestaurantNotExceeded(controller.getRestaurantWorkload()));


        }while((DataInput.yesOrNo(UsefulStrings.QUE_ADD_ANOTHER_RESERVATION) &&
                controller.restaurantNotFull((int) controller.getCovered())) &&
                controller.workloadRestaurantNotExceeded(controller.getRestaurantWorkload()));

        saveInArchiveTask(controller);

        // ora che l'agenda è stata scritta, il magazziniere potrà creare la lista della spesa a seconda delle prenotazioni raccolte
        controller.updateUserTurn();
    }

    /**
     * Metodo per quanto riguarda il salvataggio nell'archivio delle prenotazioni.
     */
    public void saveInArchiveTask(ReservationsAgentController controller){
        controller.saveInReservationArchive();
        System.out.println(UsefulStrings.OK_FILE_SAVED_MESSAGE);
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