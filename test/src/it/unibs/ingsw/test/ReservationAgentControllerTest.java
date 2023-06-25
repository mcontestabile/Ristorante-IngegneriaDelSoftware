package it.unibs.ingsw.test;


import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgentController;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class ReservationAgentControllerTest {
    @Test
    public void AgentControllerInsertReservation() throws XMLStreamException {
        ReservationsAgent agent = new ReservationsAgent("username", "password", true);

        Queue<User> users = new LinkedList<>();
        users.add(new ReservationsAgent("username", "password", true));

        ReservationsAgentController raController = new ReservationsAgentController(new UserController(users).getQueue(), agent);

        HashMap<String, String> item_list = new HashMap<>();
        item_list.put("affettati", "3");
        raController.insertReservation("Diego", "3", item_list);

        assertThat(agent.getReservations().size(), is(equalTo(1)));
    }
}
