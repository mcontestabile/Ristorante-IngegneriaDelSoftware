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
        if(!controller.getWareHouseArticles().isEmpty())
            AsciiArt.printWareHouse(controller.getWareHouseArticles());
        else
            System.out.println("Il magazzino è vuoto.");

        if(!controller.getKitchenList().isEmpty())
            AsciiArt.printKitchen(controller.getKitchenList());
    }

    /**
     *
     * @throws XMLStreamException
     */
    public void createShoppingList() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        controller.readReservations();
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
    public void getIngredientFromWareHouse() throws XMLStreamException {
        Time.pause(Time.MEDIUM_MILLIS_PAUSE);
        AsciiArt.slowPrint("Prelievo ingredienti \n");
        if(!controller.getWareHouseArticles().isEmpty()) {


            AsciiArt.printWareHouse(controller.getWareHouseArticles());

            do {
                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (controller.getArticle(name) != null) {
                    Article article = controller.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzone: max " + article.getQuantity() + article.getMeasure() + "! \n");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (controller.removeArticle(name, qty, true)) {
                        System.out.println("Prelevati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (controller.getArticle(name).getQuantity() == 0)
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
        if(!controller.getKitchenList().isEmpty()) {


            String name;
            double qty;
            //stampa degli ingredienti in cucina
            AsciiArt.printKitchen(controller.getKitchenList());

            do {
                do {
                    name = DataInput.readNotEmptyString("nome ingrediente » ");
                } while (controller.getKitchenMap().get(name) == null);
                do {
                    qty = DataInput.readDoubleWithMinimum("quantità ingrediente (max: " + controller.getKitchenMap().get(name).getQuantity() + ") »", 0);
                } while (qty > controller.getKitchenMap().get(name).getQuantity());

                if (controller.insertArticle(name, qty, controller.getKitchenMap().get(name).getMeasure())) {
                    controller.getKitchenMap().get(name).decrementQuantity(qty);
                    if(controller.getKitchenMap().get(name).getQuantity() == 0) controller.getKitchenMap().remove(name);
                    System.out.println("\nInseriti correttamente " + qty + controller.getWareHouseArticlesMap().get(name).getMeasure() + " di " + name + ".\n");
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
        if(!controller.getWareHouseArticles().isEmpty()) {

            do {

                String name = DataInput.readNotEmptyString("nome ingrediente » ");
                if (controller.getArticle(name) != null) {
                    Article article = controller.getArticle(name);

                    double qty = 0;
                    do {
                        if (qty > article.getQuantity())
                            System.out.println("Attenzione: max " + article.getQuantity() + article.getMeasure() + "!\n ");
                        qty = DataInput.readDoubleWithMinimum("qtà da prelevare (max: " + article.getQuantity() + ") » ", 0);
                    } while (qty > article.getQuantity());

                    if (controller.removeArticle(name, qty, false)) {
                        System.out.println("\nScartati correttamente " + qty + article.getMeasure() + " di " + name + ".\n");
                        if (controller.getArticle(name).getQuantity() == 0)
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
