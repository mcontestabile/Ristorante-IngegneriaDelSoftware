package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class WarehouseWorker {
    private String name;
    private String password;

    private ArrayList<ReservationItems> reservations;   //prenotazioni
    private HashMap<String, ReservationItems> reservationsMap;  //prenotazioni per nome
    private ArrayList<WareHouseArticle> wareHouseArticles = new ArrayList<>();  //articoli nel magazzino
    private HashMap<String, WareHouseArticle> wareHouseArticlesMap;     //articoli nel magazzino
    private ArrayList<CookbookRecipe> cookbookRecipes;
    private ArrayList<Recipe> recipes;
    private ArrayList<Article> shoppingList;
    private HashMap<String, Article> shoppingListMap;
    private HashMap<String, Integer> menuReservations = new HashMap<>();    //menu presenti in una prenotazione
    private ArrayList<Course> courses;  //Arraylist di menu (ogni menu ha i suoi piatti)
    private HashMap<String, Course> coursesMap;
    private HashMap<String, CookbookRecipe> recipeMap;  //ricetta di ogni piatto

    private ArrayList<Article> articles;
    private HashMap<String, Article> kitchenMap = new HashMap<>();

    private ArrayList<Drink> drinksPerCustomer;
    private ArrayList<Appetizer> appetizersPerCustomer;
    private static final int GAP = 10;

    /**
     * Variabile che determina se il magazziniere può lavorare o meno.
     */
    private boolean canIWork;


    // Questo oggetto consente il calcolo del tempo trascorso.
    Timer timer;

    /**
     * Costruttore dell'oggetto articolo
     * @param name nome del magazziniere.
     * @param password password.
     * @param canIWork determina se il magazziniere può lavorare o meno.
     */
    public WarehouseWorker(String name, String password, boolean canIWork) throws XMLStreamException {
        this.name = name;
        this.password = password;
        this.canIWork = canIWork;

        setCookbook(cookbookParsingTask());
        setCourses(coursesParsingTask());
        setWareHouseArticles(wareHouseParsingTask());
        drinksPerCustomer = drinkParsingTask();
        appetizersPerCustomer = appetizerParsingTask();
    }

    /**
     * Metodo che legge le prenotazioni
     */
    public boolean readReservations() {
        try {
            setReservations(reservationParsingTask());
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Metodo che setta il la map degli ingredienti
     */
    public void setCookbook(@NotNull ArrayList<CookbookRecipe> cookbookRecipes) {
        this.cookbookRecipes = cookbookRecipes;
        recipeMap = new HashMap<>();
        cookbookRecipes.forEach(e -> recipeMap.put(e.getName(), e));
    }

    /**
     * Metodo che setta il la map delle prenotazioni
     */
    public void setReservations(@NotNull ArrayList<ReservationItems> reservations) {
        this.reservations = reservations;
        reservationsMap = new HashMap<>();
        reservations.forEach(r -> reservationsMap.put(r.getName(), r));
    }

    /**
     * Metodo che setta il la map dei menu
     */
    public void setCourses(@NotNull ArrayList<Course> courses) {
        this.courses = courses;
        coursesMap = new HashMap<>();
        courses.forEach(c -> coursesMap.put(c.getName(), c));
    }

    /**
     * Metodo che setta il la map del magazzino
     */
    public void setWareHouseArticles(@NotNull ArrayList<WareHouseArticle> wareHouseArticles) {
        this.wareHouseArticles = wareHouseArticles;
        wareHouseArticlesMap = new HashMap<>();
        wareHouseArticles.forEach(a -> wareHouseArticlesMap.put(a.getName(), a));
    }

    /**
     * Parsing del file delle prenotazioni
     */
    public ArrayList<ReservationItems> reservationParsingTask() throws XMLStreamException {
        XMLParser reservationParser = new XMLParser(UsefulStrings.AGENDA_FILE);
        return new ArrayList<>(reservationParser.parseXML(ReservationItems.class));
    }

    /**
     * Parsing del file del ricettario
     */
    public ArrayList<CookbookRecipe> cookbookParsingTask() throws XMLStreamException {
        XMLParser cookbookParser = new XMLParser(UsefulStrings.COOKBOOK_FILE);
        return new ArrayList<>(cookbookParser.parseXML(CookbookRecipe.class));
    }

    /**
     * Parsing del file delle bevande
     */
    public ArrayList<Drink> drinkParsingTask() throws XMLStreamException {
        XMLParser drinkParser = new XMLParser(UsefulStrings.DRINKS_FILE);
        return new ArrayList<>(drinkParser.parseXML(Drink.class));
    }

    /**
     * Parsing del file degli antipasti
     */
    public ArrayList<Appetizer> appetizerParsingTask() throws XMLStreamException {
        XMLParser appetizerParser = new XMLParser(UsefulStrings.APPETIZERS_FILE);
        return new ArrayList<>(appetizerParser.parseXML(Appetizer.class));
    }

    /**
     * Parsing del file del magazzino
     */
    public ArrayList<WareHouseArticle> wareHouseParsingTask() throws XMLStreamException {
        XMLParser wareHouseParser = new XMLParser(UsefulStrings.WAREHOUSE_FILE);
        return new ArrayList<>(wareHouseParser.parseXML(WareHouseArticle.class));
    }

    /**
     * Parsing del file del menu
     */
    public ArrayList<Course> coursesParsingTask() throws XMLStreamException {
        XMLParser coursesParser = new XMLParser(UsefulStrings.COURSES_FILE);
        return new ArrayList<>(coursesParser.parseXML(Course.class));
    }

    /**
     * Writing del file del magazzino
     */
    public void warehouseWritingTask(ArrayList<Article> articles) throws XMLStreamException {
        XMLWriter writer = new XMLWriter(UsefulStrings.WAREHOUSE_FILE);
        writer.writeArrayListXML(articles, "warehouse");
    }

    /**
     * Metodo che ritorna la lista degli articoli nel magazzino
     *
     * @return articles lista di articoli
     */
    public ArrayList<Article> getWareHouseArticles() throws XMLStreamException{
        setWareHouseArticles(wareHouseParsingTask());
        articles = new ArrayList<>();
        wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
            articles.add(new Article(wareHouseArticleName, wareHouseArticle.getQuantity(), wareHouseArticle.getMeasure()));
        });
        return articles;
    }

    /**
     * Metodo che ritorna la mappa degli ingredienti in cucina
     * @return kitchenMap
     */
    public HashMap<String, Article> getKitchenMap() {
        return kitchenMap;
    }

    /**
     * Metodo che ritorna la lista degli ingredienti in cucina
     * @return kitchenArticles
     */
    public ArrayList<Article> getKitchenList() {
        ArrayList<Article> kitchenArticles = new ArrayList<>();
        kitchenMap.forEach((name, article) -> {
            kitchenArticles.add(article);
        });

        return kitchenArticles;
    }

    /**
     * Metodo che ritorna un articolo
     * @param name
     * @return Article
     */
    public Article getArticle(String name) {
        if(wareHouseArticlesMap.get(name) != null)
            return new Article(wareHouseArticlesMap.get(name).getName(),wareHouseArticlesMap.get(name).getQuantity(), wareHouseArticlesMap.get(name).getMeasure());
        else return null;
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
        setWareHouseArticles(wareHouseParsingTask());
        articles = new ArrayList<>();
        if(wareHouseArticlesMap.get(name) != null) {
            wareHouseArticlesMap.get(name).incrementQuantity(quantity);
        } else {
            WareHouseArticle wareHouseArticle = new WareHouseArticle();
            wareHouseArticle.setName(name);
            wareHouseArticle.setQuantityDouble(quantity);
            wareHouseArticle.setMeasure(measure);
            wareHouseArticlesMap.put(name, wareHouseArticle);
        }
        wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
            articles.add(new Article(wareHouseArticleName, (double) Math.round(wareHouseArticle.getQuantity() * 100.0) / 100.0, wareHouseArticle.getMeasure()));
        });
        try {
            warehouseWritingTask(articles);
            return true;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo che rimuove un articolo
     * @param name
     * @param quantity
     * @param toKitchen
     * @return true o false
     */
    public boolean removeArticle(String name, double quantity, boolean toKitchen) {

        articles = new ArrayList<>();
        if(wareHouseArticlesMap.get(name) != null) {
            if(toKitchen) {
                if(kitchenMap.get(name) != null)
                    kitchenMap.get(name).incrementQuantity(quantity);
                else
                    kitchenMap.put(name, new Article(name, quantity, wareHouseArticlesMap.get(name).getMeasure()));
            }
            wareHouseArticlesMap.get(name).decrementQuantity(quantity);

            wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
                if(wareHouseArticle.getQuantity() != 0.0)
                    articles.add(new Article(wareHouseArticleName, (double) Math.round(wareHouseArticle.getQuantity()*100.0) / 100.0, wareHouseArticle.getMeasure()));
            });
            try {
                warehouseWritingTask(articles);
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
    public HashMap <String, Article> getNecessaryIngredients() throws XMLStreamException {

        setCookbook(cookbookParsingTask());
        setCourses(coursesParsingTask());

        HashMap <String, Article> necessaryIngredients = new HashMap<>();

        //scorro tutte le prenotazioni
        reservations.forEach((r) -> {
            //scorro tutti i menu di una prenotazione
            r.getReservation_items().forEach((menuR, item_coverR) -> {   //<menu, coperti>

                //System.out.println(coursesMap.get(menuR));
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
                                qtyIngredientForCover = Integer.parseInt(ingredient[0])/recipeMap.get(d).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                                qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                                qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * GAP)/100;
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
                            //splitto ogni ingrediente della ricetta per trovarne la quantità (100g)
                            String ingredient[] = i.split(" ", 2);   //ingredient[0] -> qtà      ingredient[1] -> nome ingrediente
                            String measure = "";
                            double qtyIngredientForCover;
                            double qtyIngredientForCoverReserved;
                            if(ingredient[0].contains("g")) {
                                ingredient[0] = ingredient[0].replaceAll("\\D+", "");
                                measure = "g";
                                qtyIngredientForCover = Integer.parseInt(ingredient[0])/recipeMap.get(menuR).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                                qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                                qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * GAP)/100;
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
                            qtyIngredientForCover = Integer.parseInt(ingredient[0])/recipeMap.get(menuR).getPortionInt();   //qtà ingrediente per coperto (ingredienti a persona)
                            qtyIngredientForCoverReserved = qtyIngredientForCover * item_coverR;    //qtà ingrediente per coperti prenotati di quel menu (ingredienti per tutte le persone prenotate)
                            qtyIngredientForCoverReserved += (qtyIngredientForCoverReserved * GAP)/100;
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
    public ArrayList<Article> getNecessaryDrinks() {
        ArrayList<Article> necessaryDrinks = new ArrayList<>();
        HashMap<String, Article> necessaryDrinksMap = new HashMap<>();

        //per ogni reservation, guardo le covers e moltiplico ogni bevanda per le covers per vedere quante ne ho bisogno
        reservations.forEach((r) -> {

            drinksPerCustomer.forEach((d) -> {
                double qty = d.getQuantityDouble()*(r.getRes_cover());
                if(necessaryDrinksMap.get(d.getName()) != null)
                    necessaryDrinksMap.get(d.getName()).incrementQuantity((double)Math.round(qty*100.0)/100.0);
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
    public ArrayList<Article> getNecessaryAppetizers() {
        ArrayList<Article> necessaryAppetizers = new ArrayList<>();
        HashMap<String, Article> necessaryAppetizersMap = new HashMap<>();

        //per ogni reservation, guardo le covers e moltiplico ogni bevanda per le covers per vedere quante ne ho bisogno
        reservations.forEach((r) -> {
            appetizersPerCustomer.forEach((a) -> {
                double qty = a.getQuantityDouble()*(r.getRes_cover());
                if(necessaryAppetizersMap.get(a.getGenre()) != null)
                    necessaryAppetizersMap.get(a.getGenre()).incrementQuantity((double)Math.round(qty*100.0)/100.0);
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
    public ArrayList<Article> createShoppingList() throws XMLStreamException {
        setWareHouseArticles(wareHouseParsingTask());
        readReservations();
        HashMap<String, Article> necessaryIngredients = getNecessaryIngredients();
        ArrayList<Article> necessaryDrinks = getNecessaryDrinks();
        ArrayList<Article> necessaryAppetizers = getNecessaryAppetizers();

        shoppingList = new ArrayList<>();
        //scorro gli ingredienti necessari e vedo se è presente nel magazzino.
        necessaryIngredients.forEach((articleName, article) -> {
            if(wareHouseArticlesMap.get(articleName) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(articleName).getQuantity() - article.getQuantity();
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(articleName, (double) Math.round(Math.abs(quantityToBuy)*100.0)/100.0, article.getMeasure()));
                    // System.out.println(shoppingListMap.get(articleName));
                }
            } else {
                shoppingList.add(article);
            }
        });
        necessaryDrinks.forEach((a) -> {
            if(wareHouseArticlesMap.get(a.getName()) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(a.getName()).getQuantity() - ((double) Math.round(a.getQuantity()*100)/100);
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(a.getName(), (double) Math.round(Math.abs(quantityToBuy)*100.0)/100.0, "l"));
                }
            } else {
                shoppingList.add(a);
            }
        });
        necessaryAppetizers.forEach((a) -> {
            if(wareHouseArticlesMap.get(a.getName()) != null) {
                double quantityToBuy = wareHouseArticlesMap.get(a.getName()).getQuantity() - ((double) Math.round(a.getQuantity()*100)/100);
                if(quantityToBuy < 0.0) {
                    shoppingList.add(new Article(a.getName(), (double) Math.round(Math.abs(quantityToBuy)*100.0)/100.0, "hg"));
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
    public void buyShoppingList(ArrayList <Article> articles) throws XMLStreamException {
        articles.forEach(a -> {
            try {
                insertArticle(a.getName(), a.getQuantity(), a.getMeasure());
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Metodo che legge se il magazziniere può lavorare
     * @return true or false
     */
    public boolean isCanIWork() {return canIWork;}

    /**
     * Metodo che setta se il magazziniere può lavorare
     * @param canIWork
     */
    public void setCanIWork(boolean canIWork) {this.canIWork = canIWork;}
}