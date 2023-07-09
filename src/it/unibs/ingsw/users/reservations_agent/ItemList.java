package it.unibs.ingsw.users.reservations_agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemList {
    private Map<Item,Integer> itemList;

    public void putInList(Item i){
        itemList.put(i, i.getResCover());
    }
    private int howManyMenus = 0;
    private int getHowManyDishes = 0;

    public ItemList() {
        itemList = new HashMap<>();
    }

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

    public void setHowManyMenus(int m) {
        this.howManyMenus += m;
    }

    public int getHowManyDishes() {
        return getHowManyDishes;
    }

    public void setHowManyDishes(int d) {
        this.getHowManyDishes += d;
    }

    public void updateOccurences(Item i){
        if(i instanceof DishItem)
            setHowManyDishes(i.getResCover());
        else
            setHowManyMenus(i.getResCover());
    }
}
