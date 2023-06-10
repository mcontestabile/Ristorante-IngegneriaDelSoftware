package it.unibs.ingsw.users.reservations_agent;

import java.util.HashMap;

public class ReservationItemList extends ReservationDecorator{
    private HashMap<String, String> item_list;

    public void setItem_list(HashMap<String, String> item_list) {
        this.item_list = item_list;
    }

    protected ReservationItemList(Reservable decoratedReservation, HashMap<String, String> itemList) {
        super(decoratedReservation);
        item_list = itemList;
    }

    public String getReservation(){
        return super.getReservation() + item_list.toString();
    }
}
