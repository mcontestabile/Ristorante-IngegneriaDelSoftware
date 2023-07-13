package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Dish;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.manager.Manager;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

import javax.xml.stream.XMLStreamException;
import java.util.*;

public class WarehouseWorkerController extends UserController {

    List <Article> shoppingList;

    private WarehouseWorker warehouseWorker;

    /**
     * Metodo costruttore
     *
     * @param userQueue
     * @param warehouseWorker
     */
    public WarehouseWorkerController(Queue<User> userQueue, WarehouseWorker warehouseWorker) {
        super(userQueue);
        this.warehouseWorker = warehouseWorker;
    }

    /**
     * Metodo che legge le prenotazioni
     * @return true or false
     */
    public boolean readReservations() {
        try {
            warehouseWorker.setReservations(warehouseWorker.parsingTask(UsefulStrings.AGENDA_FILE, ReservationItems.class));
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Metodo che inserisce un articolo
     * @param name
     * @param quantity
     * @param measure
     * @return true or false
     * @throws XMLStreamException
     */
    public boolean insertArticle(String name, double quantity, String measure) throws XMLStreamException {
        warehouseWorker.setWareHouseArticles(warehouseWorker.parsingTask(UsefulStrings.WAREHOUSE_FILE, WareHouseArticle.class));
        List <Article> articles = new ArrayList<>();
        Map <String, WareHouseArticle> wareHouseArticlesMap = warehouseWorker.getWareHouseArticlesMap();
        if(wareHouseArticlesMap.get(name) != null) {
            wareHouseArticlesMap.get(name).incrementQuantity(quantity);
        } else {
            WareHouseArticle wareHouseArticle = new WareHouseArticle();
            wareHouseArticle.setName(name);
            wareHouseArticle.setQuantityDouble(quantity);
            wareHouseArticle.setMeasure(measure);
            wareHouseArticlesMap.put(name, wareHouseArticle);
        }

        warehouseWorker.setWareHouseArticlesMap(wareHouseArticlesMap);

        wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
            articles.add(new Article(wareHouseArticleName, Math.round(wareHouseArticle.getQuantity() * 100.0) / 100.0, wareHouseArticle.getMeasure()));
        });

        try {
            warehouseWorker.writingTask(articles, UsefulStrings.WAREHOUSE_FILE, "warehouse");

            return true;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo che rimuove un articolo scelto dal magazzino.
     * @param name
     * @param quantity
     * @param toKitchen
     * @return true o false
     */
    public boolean removeArticle(String name, double quantity, boolean toKitchen) throws XMLStreamException {

        List <Article> articles = new ArrayList<>();
        Map <String, WareHouseArticle> wareHouseArticlesMap = warehouseWorker.getWareHouseArticlesMap();
        Map <String, Article> kitchenMap = warehouseWorker.getKitchenMap();

        if(wareHouseArticlesMap.get(name) != null) {
            if(toKitchen) {
                if(kitchenMap.get(name) != null)
                    kitchenMap.get(name).incrementQuantity(quantity);
                else
                    kitchenMap.put(name, new Article(name, quantity, wareHouseArticlesMap.get(name).getMeasure()));
            }
            wareHouseArticlesMap.get(name).decrementQuantity(quantity);

            warehouseWorker.setWareHouseArticlesMap(wareHouseArticlesMap);
            warehouseWorker.setKitchenMap(kitchenMap);

            wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
                if(wareHouseArticle.getQuantity() != 0.0)
                    articles.add(new Article(wareHouseArticleName, Math.round(wareHouseArticle.getQuantity()*100.0) / 100.0, wareHouseArticle.getMeasure()));
            });
            try {
                warehouseWorker.writingTask(articles, UsefulStrings.WAREHOUSE_FILE, "warehouse");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

            return true;
        } else {
            return false;
        }

    }


    /**
     * Calcola gli articoli che la prenotazione richiede, ritornandone la quantità necessaria (non controlla le scorte in magazzino)
     * @return Mappa di articoli
     * @throws XMLStreamException
     */
    public Map<String, Article> getNecessaryIngredients() throws XMLStreamException {

        warehouseWorker.setCookbook(warehouseWorker.parsingTask(UsefulStrings.COOKBOOK_FILE, CookbookRecipe.class));
        warehouseWorker.setMenu(warehouseWorker.parsingTask(UsefulStrings.COURSES_FILE, Course.class));

        Map <String, Article> necessaryIngredients = new HashMap<>();
        List <ReservationItems> reservations = warehouseWorker.getReservations();
        Map <String, Course> coursesMap = warehouseWorker.getCoursesMap();
        Map <String, CookbookRecipe> recipeMap = warehouseWorker.getRecipeMap();

        //scorro tutte le prenotazioni
        reservations.forEach((r) -> {
            //scorro tutti gli items di una prenotazione
            r.getReservation_items().forEach((menuR, item_coverR) -> {   //<menu, coperti>

                if(coursesMap.get(menuR) != null) {
                    coursesMap.get(menuR).getDishesArraylist().forEach((d) -> { //scorro i piatti dato un menu

                        recipeMap.get(d).getIngredients().forEach((i) -> {  //scorro gli ingredienti
                            //splitto ogni ingrediente della ricetta per trovarne la quantità (100g)
                            String ingredient[] = i.split(" ", 2);   //ingredient[0] -> qtà      ingredient[1] -> nome ingrediente
                            String measure = "";
                            double qtyIngredientForCover;
                            double qtyIngredientForCoverReserved;
                            if(ingredient[0].contains("g")) {
                                ingredient[0] = ingredient[0].replaceAll("\\D+", "");
                                measure = "g";
                                qtyIngredientForCover = Double.parseDouble(ingredient[0])/recipeMap.get(d).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                                qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                                qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * warehouseWorker.getGap())/100;
                                qtyIngredientForCoverReserved = Math.round(qtyIngredientForCoverReserved * 100) / 100.0;
                            } else {
                                qtyIngredientForCoverReserved = Math.ceil((item_coverR * Integer.parseInt(ingredient[0])) / recipeMap.get(d).getPortionInt());
                                qtyIngredientForCoverReserved += 1;
                            }
                            if(necessaryIngredients.get(ingredient[1]) == null) {
                                necessaryIngredients.put(ingredient[1], new Article(ingredient[1], qtyIngredientForCoverReserved, measure));    // <-- g
                            } else {
                                necessaryIngredients.get(ingredient[1]).incrementQuantity(qtyIngredientForCoverReserved);
                            }
                        });
                    });
                } else if(menuR.equals("lunedì") || menuR.equals("martedì") || menuR.equals("mercoledì") || menuR.equals("giovedì") || menuR.equals("venerdì") || menuR.equals("sabato") || menuR.equals("domenica")) {

                    coursesMap.get("menu del giorno").getDishesArraylist().forEach((d) -> { //scorro i piatti dato un menu

                        recipeMap.get(d).getIngredients().forEach((i) -> {  //scorro gli ingredienti
                            //splitto ogni ingrediente della ricetta per trovarne la quantità (es: 100g)
                            String ingredient[] = i.split(" ", 2);   //ingredient[0] -> qtà      ingredient[1] -> nome ingrediente
                            String measure = "";
                            double qtyIngredientForCover;
                            double qtyIngredientForCoverReserved;
                            if(ingredient[0].contains("g")) {
                                ingredient[0] = ingredient[0].replaceAll("\\D+", "");
                                measure = "g";
                                qtyIngredientForCover = Double.parseDouble(ingredient[0])/recipeMap.get(menuR).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                                qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                                qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * warehouseWorker.getGap())/100;
                                qtyIngredientForCoverReserved = Math.round(qtyIngredientForCoverReserved * 100) / 100.0;
                            } else {
                                qtyIngredientForCoverReserved = Math.ceil((item_coverR * Integer.parseInt(ingredient[0])) / recipeMap.get(menuR).getPortionInt());
                                qtyIngredientForCoverReserved += 1;
                            }
                            if(necessaryIngredients.get(ingredient[1]) == null) {
                                necessaryIngredients.put(ingredient[1], new Article(ingredient[1], qtyIngredientForCoverReserved, measure));    // <-- g
                            } else {
                                necessaryIngredients.get(ingredient[1]).incrementQuantity(qtyIngredientForCoverReserved);
                            }
                        });
                    });
                } else {
                    //se non è un menu allora è un piatto.
                    recipeMap.get(menuR).getIngredients().forEach((i) -> {
                        String ingredient[] = i.split(" ", 2);   //ingredient[0] -> qtà      ingredient[1] -> nome ingrediente
                        String measure = "";
                        double qtyIngredientForCover;
                        double qtyIngredientForCoverReserved;
                        if(ingredient[0].contains("g")) {
                            ingredient[0] = ingredient[0].replaceAll("\\D+", "");
                            measure = "g";

                            qtyIngredientForCover = Double.parseDouble(ingredient[0])/recipeMap.get(menuR).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                            qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                            qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * warehouseWorker.getGap())/100;
                            qtyIngredientForCoverReserved = Math.round(qtyIngredientForCoverReserved * 100) / 100.0;
                        } else {
                            qtyIngredientForCoverReserved = Math.ceil((item_coverR * Integer.parseInt(ingredient[0])) / recipeMap.get(menuR).getPortionInt());
                            qtyIngredientForCoverReserved += 1;
                        }
                        if(necessaryIngredients.get(ingredient[1]) == null) {
                            necessaryIngredients.put(ingredient[1], new Article(ingredient[1], qtyIngredientForCoverReserved, measure));    // <-- g
                        } else {
                            necessaryIngredients.get(ingredient[1]).incrementQuantity(qtyIngredientForCoverReserved);
                        }
                    });
                }

            });

        });


        return necessaryIngredients;

    }

    /**
     * Metodo che ritorna le bevande necessarie
     * @return necessaryDrinks
     */
    public List<Article> getNecessaryDrinks() {
        List<Article> necessaryDrinks = new ArrayList<>();
        Map<String, Article> necessaryDrinksMap = new HashMap<>();
        List<ReservationItems> reservations = warehouseWorker.getReservations();
        List<Drink> drinksPerCustomer = warehouseWorker.getDrinks();

        //per ogni reservation, guardo le covers e moltiplico ogni bevanda per le covers per vedere quante ne ho bisogno
        reservations.forEach((r) -> {
            drinksPerCustomer.forEach((d) -> {
                double qty = d.getQuantityDouble()*(r.getRes_cover());
                if(necessaryDrinksMap.get(d.getName()) != null)
                    necessaryDrinksMap.get(d.getName()).incrementQuantity(Math.round(qty*100.0)/100.0);
                else
                    necessaryDrinksMap.put(d.getName(), new Article(d.getName(), qty, "l"));
            });
        });
        necessaryDrinksMap.forEach((name, article) -> {
            necessaryDrinks.add(article);
        });
        return necessaryDrinks;
    }

    /**
     * Metodo che ritorna gli antipasti necessari
     * @return necessaryAppetizers
     */
    public List<Article> getNecessaryAppetizers() {
        List<Article> necessaryAppetizers = new ArrayList<>();
        Map<String, Article> necessaryAppetizersMap = new HashMap<>();
        List<ReservationItems> reservations = warehouseWorker.getReservations();
        List<Appetizer> appetizersPerCustomer = warehouseWorker.getAppetizers();

        //per ogni reservation, guardo le covers e moltiplico ogni bevanda per le covers per vedere quante ne ho bisogno
        reservations.forEach((r) -> {
            appetizersPerCustomer.forEach((a) -> {
                double qty = a.getQuantityDouble()*(r.getRes_cover());
                if(necessaryAppetizersMap.get(a.getGenre()) != null)
                    necessaryAppetizersMap.get(a.getGenre()).incrementQuantity(Math.round(qty*100.0)/100.0);
                else
                    necessaryAppetizersMap.put(a.getGenre(), new Article(a.getGenre(), qty, "hg"));
            });
        });
        necessaryAppetizersMap.forEach((genre, article) -> {
            necessaryAppetizers.add(article);
        });
        return necessaryAppetizers;
    }

    /**
     * Metodo che crea la lista della spesa
     * @return shoppingList
     * @throws XMLStreamException
     */
    public List<Article> createShoppingList() throws XMLStreamException {
        warehouseWorker.setWareHouseArticles(warehouseWorker.parsingTask(UsefulStrings.WAREHOUSE_FILE, WareHouseArticle.class));
        readReservations();

        Map <String, WareHouseArticle> wareHouseArticlesMap = warehouseWorker.getWareHouseArticlesMap();
        Map<String, Article> necessaryIngredients = getNecessaryIngredients();
        List<Article> necessaryDrinks = getNecessaryDrinks();
        List<Article> necessaryAppetizers = getNecessaryAppetizers();

        shoppingList = new ArrayList<>();
        //scorro gli ingredienti necessari e vedo se è presente nel magazzino.
        necessaryIngredients.forEach((articleName, article) -> {
            if(wareHouseArticlesMap.get(articleName) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(articleName).getQuantity() - article.getQuantity();
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(articleName,  Math.round(Math.abs(quantityToBuy)*100.0)/100.0, article.getMeasure()));
                    // System.out.println(shoppingListMap.get(articleName));
                }
            } else {
                shoppingList.add(article);
            }
        });
        necessaryDrinks.forEach((a) -> {
            if(wareHouseArticlesMap.get(a.getName()) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(a.getName()).getQuantity() - (Math.round(a.getQuantity()*100)/100);
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(a.getName(),  Math.round(Math.abs(quantityToBuy)*100.0)/100.0, "l"));
                }
            } else {
                shoppingList.add(a);
            }
        });
        necessaryAppetizers.forEach((a) -> {
            if(wareHouseArticlesMap.get(a.getName()) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(a.getName()).getQuantity() - (Math.round(a.getQuantity()*100)/100);
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(a.getName(), Math.round(Math.abs(quantityToBuy)*100.0)/100.0, "hg"));
                }
            } else {
                shoppingList.add(a);
            }
        });


        return shoppingList;
    }

    /**
     * Metodo che compra la lista della spesa
     * @param articles
     * @throws XMLStreamException
     */
    public void buyShoppingList(List <Article> articles) throws XMLStreamException {
        articles.forEach(a -> {
            try {
                insertArticle(a.getName(), a.getQuantity(), a.getMeasure());
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public WarehouseWorker getUser() {return warehouseWorker;}

    public List<Course> getMenu() {return warehouseWorker.getMenu();}

    public List<Drink> getDrinks() {return warehouseWorker.getDrinks();}

    public List<CookbookRecipe> getCookbook() {return warehouseWorker.getCookbook();}

    public List<Article> getKitchenList() {return warehouseWorker.getKitchenList();}

    public Map<String, Article> getKitchenMap() {return warehouseWorker.getKitchenMap();}

    public List<Article> getWareHouseArticles() throws XMLStreamException {return warehouseWorker.getWareHouseArticles();}

    public Map<String, WareHouseArticle> getWareHouseArticlesMap() throws XMLStreamException {return warehouseWorker.getWareHouseArticlesMap();}

    public Article getArticle(String name) { return warehouseWorker.getArticle(name); }
}
