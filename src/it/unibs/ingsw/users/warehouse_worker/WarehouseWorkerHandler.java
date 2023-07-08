package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.mylib.menu_utils.Menu;
import it.unibs.ingsw.mylib.menu_utils.MenuItem;
import it.unibs.ingsw.mylib.menu_utils.Time;
import it.unibs.ingsw.mylib.utilities.AsciiArt;
import it.unibs.ingsw.mylib.utilities.DataInput;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.UserController;

import javax.xml.stream.XMLStreamException;

public class WarehouseWorkerHandler {

    private WarehouseWorkerController controller;

    public WarehouseWorkerHandler(WarehouseWorkerController controller) {
        this.controller = controller;
    }

    public void init(WarehouseWorker user) {
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
    public void createShoppingList(WarehouseWorker user) throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        user.readReservations();
        controller.shoppingList = controller.createShoppingList();

        if(!controller.shoppingList.isEmpty()) {
            AsciiArt.printShoppingList(controller.shoppingList);
            if(DataInput.yesOrNo("Procedere all'acquisto? ")) {
                controller.buyShoppingList(controller.shoppingList);
                System.out.println("Acquistati correttamente "+ controller.shoppingList.size() + " articoli.");
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

                    if (controller.removeArticle(name, qty, true)) {
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

                if (controller.insertArticle(name, qty, user.getKitchenMap().get(name).getMeasure())) {
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

                    if (controller.removeArticle(name, qty, false)) {
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
}
