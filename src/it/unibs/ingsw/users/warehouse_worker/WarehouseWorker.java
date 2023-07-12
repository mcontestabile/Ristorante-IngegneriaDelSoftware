package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.entrees.appetizers.Appetizer;
import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.cookbook.Recipe;
import it.unibs.ingsw.entrees.drinks.Drink;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.mylib.xml_utils.XMLWriter;
import it.unibs.ingsw.users.registered_users.User;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class WarehouseWorker extends User {
    private List<ReservationItems> reservations;   //prenotazioni
    private Map<String, ReservationItems> reservationsMap;  //prenotazioni per nome
    private List<WareHouseArticle> wareHouseArticles;
    private Map<String, WareHouseArticle> wareHouseArticlesMap;     //articoli nel magazzino
    private Map<String, Article> kitchenMap = new HashMap<>();
    private int gap = 10;


    /**
     * Costruttore dell'oggetto articolo
     * @param username nome del magazziniere.
     * @param password password.
     * @param canIWork determina se il magazziniere può lavorare o meno.
     */
    public WarehouseWorker(String username, String password, boolean canIWork) throws XMLStreamException {
        super(username, password, canIWork);

        setCookbook(parsingTask(UsefulStrings.COOKBOOK_FILE, CookbookRecipe.class));
        setMenu(parsingTask(UsefulStrings.COURSES_FILE, Course.class));
        setWareHouseArticles(parsingTask(UsefulStrings.WAREHOUSE_FILE, WareHouseArticle.class));
        setDrinks(parsingTask(UsefulStrings.DRINKS_FILE, Drink.class));
        setAppetizers(parsingTask(UsefulStrings.APPETIZERS_FILE, Appetizer.class));

    }

    /**
     * Metodo che setta il la map delle prenotazioni
     */
    public void setReservations(@NotNull ArrayList<ReservationItems> reservations) {
        this.reservations = reservations;
        reservationsMap = new HashMap<>();
        reservations.forEach(r -> reservationsMap.put(r.getName(), r));
    }

    public List <ReservationItems> getReservations() { return reservations; }

    /**
     * Metodo che setta il la map del magazzino
     */
    public void setWareHouseArticles(@NotNull ArrayList<WareHouseArticle> wareHouseArticles) {
        this.wareHouseArticles = wareHouseArticles;
        wareHouseArticlesMap = new HashMap<>();
        wareHouseArticles.forEach(a -> wareHouseArticlesMap.put(a.getName(), a));
    }

    public void setWareHouseArticlesMap(@NotNull Map<String, WareHouseArticle> wareHouseArticlesMap) {
        this.wareHouseArticlesMap = wareHouseArticlesMap;
    }

    /**
     * Metodo che ritorna la lista degli articoli nel magazzino
     *
     * @return articles lista di articoli
     */
    public List<Article> getWareHouseArticles() throws XMLStreamException {
        setWareHouseArticles(parsingTask(UsefulStrings.WAREHOUSE_FILE, WareHouseArticle.class));
        List <Article> articles = new ArrayList<>();
        wareHouseArticlesMap.forEach((wareHouseArticleName, wareHouseArticle) -> {
            articles.add(new Article(wareHouseArticleName, wareHouseArticle.getQuantity(), wareHouseArticle.getMeasure()));
        });
        return articles;
    }

    public Map<String, WareHouseArticle> getWareHouseArticlesMap() throws XMLStreamException {
        setWareHouseArticles(parsingTask(UsefulStrings.WAREHOUSE_FILE, WareHouseArticle.class));
        return wareHouseArticlesMap;
    }

    /**
     * Metodo che ritorna la mappa degli ingredienti in cucina
     * @return kitchenMap
     */
    public Map<String, Article> getKitchenMap() {
        return kitchenMap;
    }

    public void setKitchenMap(Map<String, Article> kitchenMap) { this.kitchenMap = kitchenMap; }

    /**
     * Metodo che ritorna la lista degli ingredienti in cucina
     * @return kitchenArticles
     */
    public List<Article> getKitchenList() {
        List<Article> kitchenArticles = new ArrayList<>();
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

    public int getGap() {
        return gap;
    }


}