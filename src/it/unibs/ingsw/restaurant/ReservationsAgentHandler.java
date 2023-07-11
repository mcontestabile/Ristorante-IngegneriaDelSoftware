package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.AsciiArt;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.reservations_agent.*;

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

        parsingTask();
        agentMenu();
    }

    /**
     * Metodo che visualizza il menu a scelta con le operazioni
     * che l'addetto alle prenotazioni può effettuare.
     */
    public void agentMenu() {
            MenuItem[] items = new MenuItem[]{
                    new MenuItem(UsefulStrings.UPDATE_AGENDA_MENU_VOICE, this::updateAgendaIfParametersNotExcedeed),
                    new MenuItem(UsefulStrings.SAVE_IN_RES_ARCHIVE_MENU_VOICE, this::saveInArchiveTask)
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
        if(controller.restaurantNotFull() && (controller.workloadRestaurantNotExceeded()))
            updateAgenda();
        else
            System.out.println(UsefulStrings.NO_MORE_RESERVATION_MESSAGE);
    }

    /**
     * Metodo che chiede all'utente un nome per la prenotazione,
     * fintantoché non venga immesso un valore che non sia già presente nella lista delle prenotazioni.
     *
     * @return nome della prenotazione
     */
    public String askName(){
        String name;

        do{
            name = DataInput.readNotEmptyString(UsefulStrings.RESERVATION_NAME);
        }while(controller.isAlreadyIn(name, controller.getReservationNameList()));

        return name;
    }

    /**
     * Metodo che chiede all'utente il numero dei coperti della prenotazione,
     * fintantiché non venga immesso un valore che non superi il massimo numero raggiungibile.
     *
     * @return coperti della prenotazione
     */
    public int askResCover(){
        int resCover;

        do{
            resCover = DataInput.readPositiveInt(UsefulStrings.RES_COVER);
        }while(controller.exceedsCover(resCover));

        return resCover;
    }

    /**
     * Metodo che chiede all'utente il nome di un item (menu/piatto)
     * fintantoché non venga immesso un valore già presente nella lista di item,
     * il nome non si trovi nel menu del ristorante,
     * il carico di lavoro del menu/piatto (esteso ad una persona) supera il carico di lavoro massimo.
     * Basta solamente una condizione per far ripetere l'inserimento.
     *
     * @param itemList, la lista di item
     * @return nome del menu/piatto valido inserito dall'utente
     */
    public String askItemName(ItemList itemList){
        String menu_piatto;

        do{
            menu_piatto = DataInput.readNotEmptyString(UsefulStrings.MENU_DISH_NAME);
        }while(controller.controlIfAskItemNameAgain(menu_piatto, itemList));

        return menu_piatto;
    }

    public int askItemCover(ItemList list, Reservable r, String itemName){
        int itemCover;

        do {
            itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
        } while (controller.itemControl(list, itemCover, itemName, r));

        return itemCover;
    }

    public boolean askMoreItemsIfNeededOrUserDecision(ItemList il, Reservable sr){
        boolean doYouWantToContinue = true;

        if(!controller.moreItemsNeeded(il.getHowManyDishes(), il.getHowManyMenus(), sr.getResCover()))
            doYouWantToContinue = DataInput.yesOrNo(UsefulStrings.MORE_ITEMS);

        return doYouWantToContinue;
    }

    public boolean itemTaskIterationControl(ItemList il, Reservable sr){
        return askMoreItemsIfNeededOrUserDecision(il, sr) && controller.workloadRestaurantNotExceeded();
    }

    /**
     * Metodo per l'aggiornamento dell'agenda riguardante le prenotazioni.
     */
    public void updateAgenda(){
        initialTask();
        buildReservation();
        saveInArchiveTask();

        // ora che l'agenda è stata scritta, il magazziniere potrà creare la lista della spesa a seconda delle prenotazioni raccolte
        controller.updateUserTurn();
    }

    private void initialTask() {
        welcome();
        seeMenus();
        seeInfos();
    }

    private void buildReservation() {
        Reservable sr;
        ItemList il;

        do{
            il = new ItemList();

            sr = controller.createSimpleReservation(askName(), askResCover());

            itemTask(sr, il);

            controller.closeResInsertion(sr, il);
            controller.writeAgenda();

        }while(controller.endUpdateAgendaIterationControl());
    }

    private void itemTask(Reservable sr, ItemList il) {
        String name;
        int cover;

        do{
            name = askItemName(il);
            cover = askItemCover(il,sr,name);

            Item item = controller.createItem(name, cover);
            il.putInList(item);
            il.updateOccurences(item);

            controller.updateCaricoRaggiunto(controller.calculateWorkload(item.getName(), item.getCover()));

        }while(itemTaskIterationControl(il, sr));
    }

    /**
     * Messaggio iniziale di benvenuto al task di aggiornamento agenda.
     */
    private static void welcome() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.UPDATE_AGENDA);
    }

    /**
     * Acquisizione del menu e dei carichi di lavoro dei menu/piatti,
     * dal relativo file.
     */
    private void parsingTask() {
        controller.parseCourses();
        controller.parseWorkloads();
    }

    /**
     * Visualizza le informazioni utili all'addetto.
     */
    private void seeInfos() {
        AsciiArt.seeInfoCovered(controller.getCopertiRaggiunti(), (int) controller.getCovered());
        AsciiArt.seeInfoWorkload(controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());
    }

    /**
     * Visualizza le informazioni riguardanti i menu.
     */
    private void seeMenus() {
        AsciiArt.printALaCarteMenu(controller.getMenu());
        AsciiArt.printThemedMenu(controller.getMenu());
    }

    /**
     * Metodo per quanto riguarda il salvataggio nell'archivio delle prenotazioni.
     */
    public void saveInArchiveTask(){
        controller.saveInReservationArchive();
        System.out.println(UsefulStrings.OK_FILE_SAVED_MESSAGE);
    }

}
