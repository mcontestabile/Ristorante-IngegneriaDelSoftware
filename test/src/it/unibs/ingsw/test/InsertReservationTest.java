package it.unibs.ingsw.test;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.users.manager.Manager;
import org.junit.Test;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.reservations_agent.*;
import org.junit.Before;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InsertReservationTest {
    ReservationsAgent agent;
    Queue<User> users;
    ReservationsAgentController controller;
    ItemList itemList;
    Manager gestore;
    List<Course> courses;

    @Before
    public void setUp(){
        agent = new ReservationsAgent("username", "password", true);
        users = new LinkedList<>();
        users.add(agent);
        controller = new ReservationsAgentController(users, agent);
        itemList = new ItemList();
        //itemList.putInList(new DishItem("pasta al pesto", 3));

        setupManager();
    }
    @Test
    public void duplicatedReservationNameTest(){
        Reservable res1;
        Reservable res2;

        res1 = controller.createReservationItemList(new SimpleReservation("test", 4), itemList);
        controller.insertReservation(res1);

        res2 = controller.createReservationItemList(new SimpleReservation("test", 2), itemList);

        if(!controller.isAlreadyIn(res2.getName(), controller.getReservationNameList()))
            controller.insertReservation(res2);

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
    @Test
    public void notDuplicatedReservationNameTest(){
        Reservable res1;
        Reservable res2;

        res1 = controller.createReservationItemList(new SimpleReservation("test", 4), itemList);
        controller.insertReservation(res1);

        res2 = controller.createReservationItemList(new SimpleReservation("foo", 2), itemList);

        if(!controller.isAlreadyIn(res2.getName(), controller.getReservationNameList()))
            controller.insertReservation(res2);

        assertThat(agent.getReservations().size(), is(equalTo(2)));
    }
    @Test
    public void resCover_restaurantEmpty_minimum(){
        agent.setCopertiRaggiunti(0);
        int resCover = 1;

        Reservable res1 = controller.createReservationItemList(new SimpleReservation("test", resCover), itemList);

        if(!controller.exceedsCover(resCover))
            controller.insertReservation(res1);

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
    @Test
    public void resCover_restaurantEmpty_maxCoveredMinusOne(){
        agent.setCopertiRaggiunti(0);
        int resCover = (int)controller.getCovered()-1;

        Reservable res1 = controller.createReservationItemList(new SimpleReservation("test", resCover), itemList);

        if(!controller.exceedsCover(resCover))
            controller.insertReservation(res1);

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
    @Test
    public void resCover_restaurantEmpty_maxCovered(){
        agent.setCopertiRaggiunti(0);
        int resCover = (int)controller.getCovered();

        Reservable res1 = controller.createReservationItemList(new SimpleReservation("test", resCover), itemList);

        if(!controller.exceedsCover(resCover))
            controller.insertReservation(res1);

        System.out.println(controller.exceedsCover(resCover));
        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
    @Test
    public void resCover_restaurantEmpty_maxWorkloadPlusOne(){
        int restaurantWorkload = 50;
        agent.setCopertiRaggiunti(0);
        int resCover = restaurantWorkload+1;

        Reservable res1 = controller.createReservationItemList(new SimpleReservation("test", resCover), itemList);

        if(!controller.exceedsCover(resCover))
            controller.insertReservation(res1);

        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void resCover_restaurantNotEmpty_insertToMaximumWorkload(){
        agent.setCopertiRaggiunti(10);
        int resCover = (int)controller.getCovered()-controller.getCopertiRaggiunti();

        Reservable res1 = controller.createReservationItemList(new SimpleReservation("test", resCover), itemList);

        if(!controller.exceedsCover(resCover))
            controller.insertReservation(res1);

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }

    public void setupManager(){
        gestore = new Manager("gestore", "test", true);
        gestore.setRestaurantWorkload(10.0);
        gestore.setCovered(50);
        users.add(gestore);

        String piatto = "amatriciana";

        setupCourses(piatto);

        setupAgentWorkloads(piatto);
    }

    private void setupCourses(String p) {
        courses = new ArrayList<>();
        Course c1 = new Course();
        c1.setName(p);
        courses.add(c1);
        agent.setMenu(courses);
    }

    private void setupAgentWorkloads(String p) {
        Workload x = new Workload();
        x.setName(p);
        x.setNumerator("2");
        x.setDenominator("3");

        ArrayList<Workload> wlist = new ArrayList<>();
        wlist.add(x);

        agent.setWorkloads(wlist);
    }

    @Test
    public void itemNameAlreadyInItemList(){
        setupManager();
        itemList.getItemList().clear();


        Item itemNameAlreadyIn = new DishItem("amatriciana", 3);
        itemList.putInList(itemNameAlreadyIn);

        if(!controller.controlIfAskItemNameAgain(itemNameAlreadyIn.getName(),itemList))
            itemList.putInList(itemNameAlreadyIn);

        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limitMinusOne_ZeroMenusAlready(){
        setupManager();
        itemList.getItemList().clear();

        int resCover = 3;

        Reservable sr = controller.createSimpleReservation("test", resCover);

        int menuCover = resCover-1;
        int n_menu_already = 0;
        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limit_ZeroMenusAlready(){
        setupManager();
        itemList.getItemList().clear();

        int resCover = 3;

        Reservable sr = controller.createSimpleReservation("test", resCover);

        int menuCover = resCover;
        int n_menu_already = 0;
        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limitPlusOne_ZeroMenusAlready(){
        setupManager();
        itemList.getItemList().clear();

        int resCover = 3;

        Reservable sr = controller.createSimpleReservation("test", resCover);

        int menuCover = resCover+1;
        int n_menu_already = 0;
        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limit_OneMenuAlready(){
        setupManager();
        itemList.getItemList().clear();

        int resCover = 3;

        Reservable sr = controller.createSimpleReservation("test", resCover);

        int menuCover = resCover;
        int n_menu_already = 1;
        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }
    @Test
    public void itemExceedsRestaurantWorkload_withZeroAlready(){
        setupManager();
        itemList.getItemList().clear();

        int itemCover = Integer.MAX_VALUE;

        controller.updateCaricoRaggiunto(0);

        Item i = new DishItem("amatriciana", itemCover);

        if(!controller.controlIfExceedsRestaurantWorkload(i.getName(), i.getCover()))
            itemList.putInList(i);

        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }

    /*
    @Test
    public void itemExceedsRestaurantWorkload_withSomeAlready(){
        setupManager();
        itemList.getItemList().clear();

        controller.updateCaricoRaggiunto(9.0);

        Item i = new DishItem("amatriciana", 1);

        double itemWorkloadSum = controller.getCaricoRaggiunto();

        if(!controller.controlIfExceedsRestaurantWorkload(itemWorkloadSum, controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()))
            itemList.putInList(i);


        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }

    DA RIFARE

    @Test
    public void itemNotExceedsRestaurantWorkload_Limit(){
        setupManager();
        itemList.getItemList().clear();

        controller.updateCaricoRaggiunto(controller.getRestaurantWorkload() - controller.getMinimumWorkload());

        Item i = new DishItem("amatriciana", 1);

        double itemWorkloadSum = controller.getMinimumWorkload();

        if(!controller.controlIfExceedsRestaurantWorkload(itemWorkloadSum, controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()))
            itemList.putInList(i);

        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    */


    /*DA RIFARE

    public void itemExceedsRestaurantWorkload_LimitExcedeed(){
        setupManager();
        itemList.getItemList().clear();

        controller.updateCaricoRaggiunto(controller.getRestaurantWorkload());

        Item i = new DishItem("amatriciana", 1);

        double itemWorkloadSum = controller.getMinimumWorkload();

        if(!controller.controlIfExceedsRestaurantWorkload(itemWorkloadSum, controller.getCaricoRaggiunto(), controller.getRestaurantWorkload()))
            itemList.putInList(i);

        System.out.println(itemWorkloadSum);
        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }*/
}
