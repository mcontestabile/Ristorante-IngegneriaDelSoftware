package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReservationItems implements Parsable {

    private String name;
    private int res_cover;
    private String item_name;
    private int item_cover;



    //<Menù, coperti>
    private HashMap<String, Integer> reservation_items = new HashMap<>();
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    public static final String START_STRING = "reservation";


    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("res_cover");
        ATTRIBUTE_STRINGS.add("item_name");
        ATTRIBUTE_STRINGS.add("item_cover");
    }

    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setRes_cover);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setItem_name);
        setters.put(ATTRIBUTE_STRINGS.get(3), this::setItem_coverAndSave);
    }

    @Override
    public String getStartString() {
        return START_STRING;
    }

    public String getName() {return name;}
    public int getRes_cover() {return res_cover;}

    public void setName(String name) {this.name = name;}
    public void setRes_cover(String res_cover) {this.res_cover = Integer.parseInt(res_cover);}

    public void addReservation_Item() {reservation_items.put(item_name, item_cover);}
    public void setItem_name(String item_name) {this.item_name = item_name;}
    public void setItem_coverAndSave(String item_cover) {
        this.item_cover = Integer.parseInt(item_cover);
        addReservation_Item();
    }
    public HashMap<String, Integer> getReservation_items() {return reservation_items;}


    @Override
    public String toString() {

        String toString = "Recipe{" +
                "name='" + name + '\'' +
                ", res_cover='" + res_cover + '\'';

        for(Map.Entry r : reservation_items.entrySet()) {
            toString+= "[" + (String)r.getKey() + ", n=" + r.getValue() + "]  ";
        }

        toString+= "]";


        return toString;
    }
}
