package it.unibs.ingsw.users.registered_users;

import it.unibs.ingsw.users.manager.Manager;

import java.util.LinkedList;
import java.util.Queue;

public class UserController {
    /*
     * Per implementare la logica di precedenza tra gli utenti,
     * utilizziamo un approccio basato su una coda/lista ordinata.
     */
    private final Queue<User> users;
    private UserDAO model;

    public UserController(Queue<User> userQueue) {
        this.users = userQueue;
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

    public User getUserFromQueue(Queue<User> userQueue, String username) {
        for (User user : userQueue) {
            if (user.getUsername().equals(username)) {
                return user; // Restituisci l'utente trovato
            }
        }
        return null; // L'utente non è stato trovato
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

    public boolean getCanIWork(User user) {
        return user.isCanIWork();
    }

    public Queue<User> getQueue() {
        return users;
    }

    public double getWorkloadPerPerson() {return getUserFromQueue(getQueue(), "gestore").getWorkloadPerPerson();}

    public double getCovered() {return getUserFromQueue(getQueue(), "gestore").getCovered();}

    public void setCovered(int covered) {getUserFromQueue(getQueue(), "gestore").setCovered(covered);}

    public double getRestaurantWorkload() {return getUserFromQueue(getQueue(), "gestore").getRestaurantWorkload();}

    public void setRestaurantWorkload(double w) {getUserFromQueue(getQueue(), "gestore").setRestaurantWorkload(w);}
}