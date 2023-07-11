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
    Queue<User> users = new LinkedList<>();
    ReservationsAgentController controller;
    ItemList itemList;
    Manager gestore;
    List<Course> courses;
    List<Workload> workloadList;

    public void setupManager(){
        gestore = new Manager("gestore", "test", true);
        gestore.setRestaurantWorkload(10.0);
        gestore.setCovered(50);
        users.add(gestore);
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

        workloadList = new ArrayList<>();
        workloadList.add(x);

        agent.setWorkloads(workloadList);
    }
    @Before
    public void setUp(){
        setupManager();

        agent = new ReservationsAgent("username", "password", true);
        users.add(agent);
        controller = new ReservationsAgentController(users, agent);
        itemList = new ItemList();
        setupCourses("amatriciana");
        setupAgentWorkloads("amatriciana");
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

    @Test
    public void itemNameInvalid(){
        itemList.getItemList().clear();

        Item item = new DishItem("amatriciana", 3);
        itemList.putInList(item);

        if(!controller.invalidItemName(item.getName(),itemList))
            itemList.putInList(item);

        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverNotExceedsOneMenuPerPerson_limitMinusOne_ZeroMenusAlready(){
        int n_menu_already = 0;
        int resCover = 3;
        int menuCover = resCover-1;

        itemList.getItemList().clear();

        Reservable sr = controller.createSimpleReservation("test", resCover);

        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverNotExceedsOneMenuPerPerson_limit_ZeroMenusAlready(){
        int resCover = 3;
        int n_menu_already = 0;
        int menuCover = resCover;

        itemList.getItemList().clear();

        Reservable sr = controller.createSimpleReservation("test", resCover);

        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limitPlusOne_ZeroMenusAlready(){
        int resCover = 3;
        int menuCover = resCover+1;
        int n_menu_already = 0;

        itemList.getItemList().clear();

        Reservable sr = controller.createSimpleReservation("test", resCover);

        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }
    @Test
    public void itemCoverExceedsOneMenuPerPerson_limit_OneMenuAlready(){
        int resCover = 3;
        int menuCover = resCover;
        int n_menu_already = 1;

        itemList.getItemList().clear();

        Reservable sr = controller.createSimpleReservation("test", resCover);

        Item menu = new ThMenuItem("menu", menuCover);

        if(!controller.exceedsOneMenuPerPerson(menuCover, n_menu_already, sr.getResCover()))
            itemList.putInList(menu);


        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }
    @Test
    public void itemExceedsRestaurantWorkload_maxItemCover_withZeroAlready(){
        int itemCover = Integer.MAX_VALUE;
        controller.updateCaricoRaggiunto(0);

        itemList.getItemList().clear();

        Item i = new DishItem("amatriciana", itemCover);

        if(!controller.controlIfItemExceedsRestaurantWorkload(i.getName(), i.getCover()))
            itemList.putInList(i);

        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }


    @Test
    public void itemNotExceedsRestaurantWorkload_withSomeAlready(){
        itemList.getItemList().clear();

        controller.updateCaricoRaggiunto(9.0);

        Item i = new DishItem("amatriciana", 1);

        if(!controller.controlIfItemExceedsRestaurantWorkload(i.getName(), i.getCover()))
            itemList.putInList(i);


        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }

    public void newDish(String name, String numerator, String denominator){
        Course c = new Course();
        c.setName(name);
        courses.add(c);

        Workload w = new Workload();
        w.setName(name);
        w.setNumerator(numerator);
        w.setDenominator(denominator);
        workloadList.add(w);
        agent.setMenu(courses);
        agent.setWorkloads(workloadList);
    }
    @Test
    public void itemNotExceedsRestaurantWorkload_Limit(){
        double caricoRaggiunto = 0.5;

        newDish("test", "1", "2");

        itemList.getItemList().clear();

        gestore.setRestaurantWorkload(50.0);
        agent.setCaricoRaggiunto(0);

        // should be ->  caricoRaggiunto = 50 - 0.05 = 49.95  ->  sono al limite
        controller.updateCaricoRaggiunto(controller.getRestaurantWorkload() - caricoRaggiunto);


        Item i = new DishItem("test", 1);

        // should be true  ->  49.95 + 0.05  ->  arriva al limite
        if(!controller.controlIfItemExceedsRestaurantWorkload(i.getName(), i.getCover()))
            itemList.putInList(i);

        assertThat(itemList.getItemList().size(), is(equalTo(1)));
    }
    @Test
    public void itemExceedsRestaurantWorkload_LimitExcedeed(){
        itemList.getItemList().clear();

        controller.updateCaricoRaggiunto(controller.getRestaurantWorkload());

        Item i = new DishItem("amatriciana", 1);

        if(!controller.controlIfItemExceedsRestaurantWorkload(i.getName(), i.getCover()))
            itemList.putInList(i);

        assertThat(itemList.getItemList().size(), is(equalTo(0)));
    }
    @Test
    public void restaurantFull_NoInsertReservation(){
        gestore.setCovered(50);

        boolean full = controller.restaurantNotFull();
        if(!controller.restaurantNotFull())
            controller.insertReservation(new ReservationItemList(new SimpleReservation("test", 3), itemList));

        assertThat(full, is(equalTo(true)));
        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
    @Test
    public void workloadExceeded_NoInsertReservation(){
        gestore.setRestaurantWorkload(30.0);
        agent.setCaricoRaggiunto(0.0);
        controller.updateCaricoRaggiunto(30.0);

        boolean notExceeded = controller.workloadRestaurantNotExceeded();
        if(controller.workloadRestaurantNotExceeded())
            controller.insertReservation(new ReservationItemList(new SimpleReservation("test", 3), itemList));

        assertThat(notExceeded, is(equalTo(false)));
        assertThat(agent.getReservations().size(), is(equalTo(0)));
    }
}
