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
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorkerController;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorkerHandler;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import java.util.*;

/**
 * Questa classe è quella che permette un dialogo con gli utenti,
 * i quali si occupano del funzionamento ottimale del ristorante.
 */
public class Handler {

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
        if(RestaurantDates.isHoliday(RestaurantDates.today)) {
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
                case "magazziniere" -> warehouseWorkerTask((WarehouseWorker) user, controller);
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
     * permette l'aggiunta delle prenotazioni e il loro salvataggio nell'agenda delle prenotazioni,
     * nonché il salvataggio di quest'ultime nell'apposito archivio.
     */
    public void reservationsAgentTask(ReservationsAgent user, UserController controller){
        ReservationsAgentHandler handler = new ReservationsAgentHandler(new ReservationsAgentController(controller.getQueue(), user));
        handler.runAgentIfCanWork(user, controller);
    }

    /**
     * Metodo rappresentativo dell'interazione con il magazziniere, il quale
     * permette attraverso la lettura delle prenotazioni di creare la lista della spesa,
     * basando la scelta dei prodotti attraverso lo stato del magazzino.
     * Inoltre permette di portare in cucina, riportare in magazzino o scartare un articolo.
     */
    public void warehouseWorkerTask(WarehouseWorker user, UserController controller) {
        WarehouseWorkerHandler handler = new WarehouseWorkerHandler(new WarehouseWorkerController(controller.getQueue(), user));
        handler.init(user);
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