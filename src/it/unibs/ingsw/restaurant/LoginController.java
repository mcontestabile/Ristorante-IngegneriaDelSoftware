package it.unibs.ingsw.restaurant;

import it.unibs.ingsw.mylib.xml_utils.Parsable;
import it.unibs.ingsw.users.registered_users.User;
import it.unibs.ingsw.users.registered_users.UserController;

/**
 * LoginController si occupa di gestire le richieste dell'utente e
 * di interagire con il Model per ottenere i dati necessari. Quando
 * si richiede l'autenticazione di un utente, il Controller può chiamare
 * il metodo getUserCredentials() di UserDAO per ottenere le credenziali degli utenti.
 */
public class LoginController {
    private UserController userController;

    public LoginController(UserController userController) {
        this.userController = userController;
    }

    public User authenticateUser(String username, String password) {
        User user = userController.authenticateUser(username, password);
        return user;
    }
}
