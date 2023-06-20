package it.unibs.ingsw.users.registered_users;

import it.unibs.ingsw.mylib.utilities.UsefulStrings;
import it.unibs.ingsw.mylib.xml_utils.XMLParser;
import it.unibs.ingsw.users.manager.Manager;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * User Data Access Object, responsabile del recupero e del parsing delle credenziali degli utenti dal file XML.
 */
public class UserDAO {
    /**
     * Effettuiamo il parsing dell'XML e restituiamo le credenziali degli utenti come una lista.
     * @return la lista degli utenti che hanno il permesso di autenticarsi.
     * @throws XMLStreamException
     */
    public static List<UserCredentials> getUserCredentials() throws XMLStreamException {
        XMLParser usersParser = new XMLParser(UsefulStrings.USERS_FILE);
        return usersParser.parseXML(UserCredentials.class);
    }

    public String getUserCategory(User u) {
        try {
            List<UserCredentials> usersCredentials = getUserCredentials();
            for (UserCredentials uc : usersCredentials) {
                if (u.getUsername().contentEquals(uc.getUsername()))
                    return uc.getCategory();
            }
        } catch (XMLStreamException e) {
            System.out.println("Errore nel Parsing di UsersAllowed.xml");
        }

        return "";
    }

    public static Queue<User> configureUsers() {
        try {
            Queue<User> users = new LinkedList<>();
            users.add(new Manager(UserDAO.getUserCredentials().get(0).getUsername(), UserDAO.getUserCredentials().get(0).getPassword(), true));
            users.add(new ReservationsAgent(UserDAO.getUserCredentials().get(1).getUsername(), UserDAO.getUserCredentials().get(1).getPassword(), false));
            users.add(new WarehouseWorker(UserDAO.getUserCredentials().get(2).getUsername(), UserDAO.getUserCredentials().get(2).getPassword(), false));
            return users;
        } catch (XMLStreamException e) {
            System.out.println("Errore nel Parsing di UsersAllowed.xml");
        }
        return null;
    }
}
