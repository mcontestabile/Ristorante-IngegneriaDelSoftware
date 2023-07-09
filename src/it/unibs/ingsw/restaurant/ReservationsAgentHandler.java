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

    public String askName(){
        String name;

        do{
            name = DataInput.readNotEmptyString(UsefulStrings.RESERVATION_NAME);
        }while(controller.isAlreadyIn(name, controller.getReservationNameList()));

        return name;
    }

    public int askResCover(){
        int resCover;

        do{
            resCover = DataInput.readPositiveInt(UsefulStrings.RES_COVER);
        }while(controller.exceedsCover(resCover, controller.getCopertiRaggiunti(), (int) controller.getCovered()));

        return resCover;
    }

    public String askItemName(ItemList itemList){
        String menu_piatto;

        do{
            menu_piatto = DataInput.readNotEmptyString(UsefulStrings.MENU_DISH_NAME);
        }while(controlIfAskItemNameAgain(menu_piatto, itemList));

        return menu_piatto;
    }

    public boolean controlIfAskItemNameAgain(String menu_piatto, ItemList itemList){
        return !controller.isInMenu(menu_piatto) ||
                controller.isAlreadyIn(menu_piatto, itemList.getItemsName()) ||
                controller.exceedsRestaurantWorkload(controller.calculateWorkload(menu_piatto, 1), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());
    }

    public boolean control(ItemList list, int itemCover, String itemName, Reservable r){
        if(!controller.isDish(itemName))
            return controlForMenu(itemCover, list, itemName, r);
        else
            return controlForDish(itemCover, itemName);
    }
    public boolean controlForMenu(int itemCover, ItemList il, String menuName, Reservable r){
        return controller.exceedsOneMenuPerPerson(itemCover, il.getHowManyMenus(), r.getResCover()) ||
                controller.exceedsRestaurantWorkload(controller.calculateWorkload(menuName, itemCover), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());
    }
    public boolean controlForDish(int itemCover, String dishName){
        return controller.exceedsRestaurantWorkload(controller.calculateWorkload(dishName, itemCover), controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());
    }
    public int askItemCover(ItemList list, Reservable r, String itemName){
        int itemCover;

        do {
            itemCover = DataInput.readPositiveInt(UsefulStrings.MENU_DISH_COVER);
        } while (control(list, itemCover, itemName, r));

        return itemCover;
    }

    public Item createItem(ItemList il, Reservable r){
        String n = askItemName(il);

        if(!controller.isDish(n)){
            return new ItemListMenu(n, askItemCover(il, r, n));
        }else{
            return new DishItem(n, askItemCover(il, r, n));
        }
    }

    public boolean doYouWantToContinue(ItemList il, Reservable sr){
        boolean doYouWantToContinue = true;

        if(!controller.moreItemsNeeded(il.getHowManyDishes(), il.getHowManyMenus(), sr.getResCover()))
            doYouWantToContinue = DataInput.yesOrNo(UsefulStrings.MORE_ITEMS);

        return doYouWantToContinue;
    }

    public boolean againControl(ItemList il, Reservable sr){
        return doYouWantToContinue(il, sr) && controller.workloadRestaurantNotExceeded(controller.getRestaurantWorkload());
    }

    public boolean lastControl(){
        return (DataInput.yesOrNo(UsefulStrings.QUE_ADD_ANOTHER_RESERVATION) &&
                controller.restaurantNotFull((int) controller.getCovered())) &&
                controller.workloadRestaurantNotExceeded(controller.getRestaurantWorkload());
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
        parsingTask();
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

            seeInfos();

            itemTask(sr, il);

            closeResInsertion(sr, il);

        }while(lastControl());
    }

    private void closeResInsertion(Reservable sr, ItemList il) {
        ReservationItemList res = controller.createReservationItemList(sr, il);
        controller.insertReservation(res);
        controller.updateCopertiRaggiunti(res.getResCover());
        controller.writeAgenda();
    }

    private void itemTask(Reservable sr, ItemList il) {
        Item item;
        do{
            item = createItem(il, sr);
            il.putInList(item);
            il.updateOccurences(item);

            controller.updateCaricoRaggiunto(controller.calculateWorkload(item.getName(), item.getResCover()));
        }while(againControl(il, sr));
    }

    private static void welcome() {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint(UsefulStrings.UPDATE_AGENDA);
    }

    private void parsingTask() {
        controller.parseCourses();
        controller.parseWorkloads();
    }

    private void seeInfos() {
        AsciiArt.seeInfoCovered(controller.getCopertiRaggiunti(), (int) controller.getCovered());
        AsciiArt.seeInfoWorkload(controller.getCaricoRaggiunto(), controller.getRestaurantWorkload());
    }

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
