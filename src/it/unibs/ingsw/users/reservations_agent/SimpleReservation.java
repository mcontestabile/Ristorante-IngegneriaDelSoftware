package it.unibs.ingsw.users.reservations_agent;

public class SimpleReservation implements Reservable{

    private String name;
    private String res_cover;

    public void setName(String name) {
        this.name = name;
    }

    public void setRes_cover(String res_cover) {
        this.res_cover = res_cover;
    }

    @Override
    public String getReservation() {
        return name+": "+res_cover;
    }
}
