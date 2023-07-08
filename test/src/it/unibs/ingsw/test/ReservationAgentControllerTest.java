package it.unibs.ingsw.test;


import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.reservations_agent.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReservationAgentControllerTest {
    ReservationsAgent agent;
    Queue<User> users;
    ReservationsAgentController controller;
    Map<String, String> itemList;

    @Before
    public void setUp(){
        agent = new ReservationsAgent("username", "password", true);
        users = new LinkedList<>();
        users.add(agent);
        controller = new ReservationsAgentController(users, agent);
        itemList = new HashMap<>();
        itemList.put("pasta", "3");
    }

    @Test
    public void simpleReservationCreationTest(){
        SimpleReservation r = controller.createSimpleReservation("test", 3);

        assertThat(r.getName(), is(equalTo("test")));
        assertThat(r.getResCover(), is(equalTo(3)));
    }

    @Test
    public void reservationItemListCreationTest(){
        Reservable r = controller.createSimpleReservation("test", 3);
        ReservationItemList res = controller.createReservationItemList(r, itemList);

        assertThat(res.getName(), is(equalTo("test")));
        assertThat(res.getResCover(), is(equalTo(3)));
    }

    @Test
    public void simpleReservationCreationNameTest(){
        Reservable res1 = controller.createSimpleReservation("test1", 6);
        res1 = controller.createReservationItemList(res1, itemList);

        controller.insertReservation(res1);

        Reservable res2 = controller.createSimpleReservation("test1", 6);
        res2 = controller.createReservationItemList(res1, itemList);

        if(!controller.isAlreadyIn("test1", controller.getReservationNameList())){
            controller.insertReservation(res2);
        }

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }

    @Test
    public void ResCoverMaxMinusOneWithZeroAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(0);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock-1);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }

    @Test
    public void ResCoverMaxWithZeroAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(0);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }

    @Test
    public void ResCoverMaxPlusOneWithZeroAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(0);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock+1);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void ResCoverMaxMinusOneWithAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(10);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock-1);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void ResCoverMaxWithAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(10);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void ResCoverMaxPlusOneWithAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(10);

        Reservable res1 = controller.createSimpleReservation("test1", restaurantCoverMock+1);
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void ResCoverWithAlreadyIn(){
        int restaurantCoverMock = 50;
        agent.setCopertiRaggiunti(10);

        Reservable res1 = controller.createSimpleReservation("test1", (restaurantCoverMock - agent.getCopertiRaggiunti()));
        res1 = controller.createReservationItemList(res1, itemList);

        if(!controller.exceedsCover(res1.getResCover(), controller.getCopertiRaggiunti(), restaurantCoverMock)){
            controller.insertReservation(res1);
        }

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
}
