package it.unibs.ingsw.users.reservations_agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe per gestire la lista di item di una prenotazione completa.
 */
public class ItemList {
    /**
     * La lista caratterizzata da una chiave di tipo Item
     * e da una value di tipo Integer che sta ad indicare
     * per quanti coperti è stato scelto.
     */
    private Map<Item,Integer> itemList;

    public void putInList(Item i){
        itemList.put(i, i.getCover());
    }

    /**
     * Occorrenze dei menu nella lista di item.
     */
    private int howManyMenus = 0;
    /**
     * Occorrenze dei piatti nella lista di item.
     */
    private int getHowManyDishes = 0;

    public ItemList() {
        itemList = new HashMap<>();
    }

    /**
     * Metodo che restituisce la sola lista dei nomi degli item
     * presenti nella lista.
     *
     * @return lista dei soli nomi degli item.
     */
    public List<String> getItemsName(){
        return itemList
                .keySet()
                .stream()
                .map(item -> item.getName())
                .collect(Collectors.toList());
    }

    public Map<Item, Integer> getItemList() {
        return itemList;
    }

    public int getHowManyMenus() {
        return howManyMenus;
    }

    public void updateHowManyMenusBy(int m) {
        this.howManyMenus += m;
    }

    public int getHowManyDishes() {
        return getHowManyDishes;
    }

    public void updateHowManyDishesBy(int d) {
        this.getHowManyDishes += d;
    }

    public void updateOccurences(Item i){
        if(i instanceof DishItem)
            updateHowManyDishesBy(i.getCover());
        else
            updateHowManyMenusBy(i.getCover());
    }
}
