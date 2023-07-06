package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.AsciiArt;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgentController;

import java.util.HashMap;

public class ReservationsAgentHandler {
    private ReservationsAgentController controller;

    public ReservationsAgentHandler(ReservationsAgentController controller) {
        this.controller = controller;
    }

    /**
     * Metodo che controlla se l'agente può lavorare (previa configurazione del ristorante da parte del manager).
     * In caso positivo, l'utente verrà condotto alla visualizzazione dei menu di scelta del relativo user (agent).
     *
     * @param user l'utente in questione
     * @param userController il controller degli utenti
     */
    public void runAgentIfCanWork(ReservationsAgent user, UserController userController){
        if(!userController.getCanIWork(user))
            AsciiArt.slowPrint(UsefulStrings.ACCESS_DENIED4);
        else
            agentMenu();
    }

    /**
     * Metodo che visualizza il menu a scelta con le operazioni
     * che l'addetto alle prenotazioni può effettuare.
     */
    public void agentMenu() {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.UPDATE_AGENDA_MENU_VOICE, () -> updateAgendaIfParametersNotExcedeed()),
                    new MenuItem(UsefulStrings.SAVE_IN_RES_ARCHIVE_MENU_VOICE, () -> saveInArchiveTask())
            };

            Menu menu = new Menu(UsefulStrings.MAIN_TASK_REQUEST, items);
            menu.changeExitText(UsefulStrings.BACK_MENU_OPTION2);
            menu.run();
    }

    /**
     * Metodo che conduce all'operazione di aggiornamento dell'agenda,
     * previo controllo dei parametri (coperti non superati, carico di lavoro non superato).
     */
    public void updateAgendaIfParametersNotExcedeed(){
        if((controller.getCopertiRaggiunti() < controller.getCovered())
                && (controller.getCaricoRaggiunto() < controller.getRestaurantWorkload()))
            updateAgenda();
        else
            System.out.println(UsefulStrings.NO_MORE_RESERVATION_MESSAGE);
    }

    /**
     * Metodo per l'aggiornamento dell'agenda riguardante le prenotazioni.
     */
    public void updateAgenda(){
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


            AsciiArt.seeInfoCovered(controller.getCopertiRaggiunti(), (int) controller.getCovered());
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

        saveInArchiveTask();

        // ora che l'agenda è stata scritta, il magazziniere potrà creare la lista della spesa a seconda delle prenotazioni raccolte
        controller.updateUserTurn();
    }

    /**
     * Metodo per quanto riguarda il salvataggio nell'archivio delle prenotazioni.
     */
    public void saveInArchiveTask(){
        controller.saveInReservationArchive();
        System.out.println(UsefulStrings.OK_FILE_SAVED_MESSAGE);
    }

}
