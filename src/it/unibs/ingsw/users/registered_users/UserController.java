package it.unibs.ingsw.users.registered_users;

import it.unibs.ingsw.users.User;
import it.unibs.ingsw.users.manager.Manager;
import it.unibs.ingsw.users.reservations_agent.ReservationsAgent;
import it.unibs.ingsw.users.warehouse_worker.WarehouseWorker;

import javax.xml.stream.XMLStreamException;
import java.util.LinkedList;
import java.util.Queue;

public class UserController {
    /*
     * Per implementare la logica di precedenza tra gli utenti,
     * utilizziamo un approccio basato su una coda/lista ordinata.
     */
    private final Queue<User> users;
    private UserDAO model;

    public UserController() {
        users = new LinkedList<>();
        model = new UserDAO();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User authenticateUser(String givenUsername, String givenPassword) {
        User user = findUserByUsername(givenUsername);

        if (user != null && user.getPassword().equals(givenPassword)) {
            if (user.isCanIWork()) {
                return user;
            } else {
                return null; // Utente non può lavorare al momento
            }
        }
        return null; // Autenticazione fallita
    }

    public void updateUserTurn() {
        User currentUser = users.poll();
        currentUser.setCanIWork(false);
        users.offer(currentUser);
        User nextUser = users.peek();
        nextUser.setCanIWork(true);
    }

    private User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().contentEquals(username))
                return u;
        }
        return null;
    }

    public String findUserCategory(User user) {
        return model.getUserCategory(user);
    }

    public void configureUsers() {
        try {
            addUser(new Manager(UserDAO.getUserCredentials().get(0).getUsername(), UserDAO.getUserCredentials().get(0).getPassword(), true));
            addUser(new ReservationsAgent(UserDAO.getUserCredentials().get(1).getUsername(), UserDAO.getUserCredentials().get(1).getPassword(), false));
            addUser(new WarehouseWorker(UserDAO.getUserCredentials().get(2).getUsername(), UserDAO.getUserCredentials().get(2).getPassword(), false));
        } catch (XMLStreamException e) {
            System.out.println("Errore nel Parsing di UsersAllowed.xml");
        }
    }

    public boolean getCanIWork(User user) {
        return user.isCanIWork();
    }

    public Queue<User> getUsers() {
        return users;
    }
}
