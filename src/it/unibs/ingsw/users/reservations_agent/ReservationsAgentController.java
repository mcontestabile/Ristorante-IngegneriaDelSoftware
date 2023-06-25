package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.entrees.resturant_courses.Course;
import it.unibs.ingsw.entrees.resturant_courses.Workload;
import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

import javax.xml.stream.XMLStreamException;
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

    public double getCaricoRaggiunto() {return agent.getCaricoRaggiunto();}

    public void parseCourses(){
        try {
            agent.setMenu(agent.parsingTask(UsefulStrings.COURSES_FILE, Course.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    public void parseWorkloads(){
        try {
            agent.setWorkloads(agent.parsingTask(UsefulStrings.WORKLOADS_FILE, Workload.class));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
