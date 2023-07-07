package it.unibs.ingsw.test;

import it.unibs.ingsw.entrees.cookbook.CookbookRecipe;
import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Dish;
import it.unibs.ingsw.mylib.utilities.Fraction;
import it.unibs.ingsw.users.manager.Manager;
import it.unibs.ingsw.users.manager.ManagerController;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgentController;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * ManagerController Tester.
 *
 * @author <Authors name>
 * @since <pre>Jul 7, 2023</pre>
 * @version 1.0
 */
public class ManagerControllerTest {

    /**
     *
     * Method: checkMenuWorkload(@NotNull Fraction menuWorkload)
     *
     */
    @Test
    public void testCheckMenuWorkload() throws Exception {
        Manager manager = new Manager("gestore", "esempio", true);
        Queue<User> users = new LinkedList<>();
        users.add(new Manager("gestore", "esempio", true));

        UserController controller = new ManagerController(users, manager);

        Fraction menuWorkload = new Fraction(2, 3);

        manager.setWorkloadPerPerson(3);

        assertThat(((ManagerController) controller).checkMenuWorkload(menuWorkload), is(true));
    }

    /**
     *
     * Method: checkMenuWorkload(@NotNull Fraction menuWorkload)
     *
     */
    @Test
    public void testCheckIncorrectMenuWorkload() throws Exception {
        Manager manager = new Manager("gestore", "esempio", true);
        Queue<User> users = new LinkedList<>();
        users.add(new Manager("gestore", "esempio", true));

        UserController controller = new ManagerController(users, manager);

        Fraction menuWorkload = new Fraction(7, 3);

        manager.setWorkloadPerPerson(2);

        assertThat(((ManagerController) controller).checkMenuWorkload(menuWorkload), is(false));
    }
} 
