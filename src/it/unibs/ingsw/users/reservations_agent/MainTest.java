package it.unibs.ingsw.users.reservations_agent;

import java.util.HashMap;

public class MainTest {
    public static void main(String[] args) {
        Reservable r = new SimpleReservation();

        ((SimpleReservation) r).setName("Anto");
        ((SimpleReservation) r).setRes_cover("10");

        System.out.println(r.getReservation());

        HashMap<String, String> items = new HashMap<>();

        items.put("carciofi", "3");
        items.put("funghi", "5");

        r = new ReservationItemList(r, items);

        System.out.println(r.getReservation());
    }
}
