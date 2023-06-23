package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

import java.util.Queue;

public class ReservationsAgentController extends UserController {
    private ReservationsAgent agent;

    /**
     * Costruttore del controller.
     * @param userQueue la lista degli utenti con cui interagisce.
     * @param agent l'utente specifico di questo controller.
     */
    public ReservationsAgentController(Queue<User> userQueue, ReservationsAgent agent) {
        super(userQueue);
        this.agent = agent;
    }

    public double getCarico_raggiunto() {return agent.getCarico_raggiunto();}
}
