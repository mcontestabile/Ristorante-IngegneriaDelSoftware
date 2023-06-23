package it.unibs.ingsw.mylib.menu_utils;

import it.unibs.ingsw.users.registered_users.UserController;

import java.util.function.Consumer;

/**
 * {@code MenuItem} rappresenta una classe necessaria per la classe {@code Handler}.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub></a>
 * @see Menu
 */
public class MenuItem<T> {
    private String text;
    private Runnable function; // Il menu utente è gestito da un thread.
    private Consumer<T> functionAlternative;

    /**
     * Costruttore di {@code MenuItem}.
     *
     * @param text testo dell'opzione che si può scegliere.
     * @param function metodo associato al testo.
     */
    public MenuItem(String text, Runnable function) {
        this.text = text;
        this.function = function;
    }

    /**
     * Costruttore di {@code MenuItem}.
     *
     * @param text testo dell'opzione che si può scegliere.
     * @param functionAlternative metodo associato al testo.
     */
    public MenuItem(String text, Consumer<T> functionAlternative) {
        this.text = text;
        this.functionAlternative = functionAlternative;
    }

    /**
     * @return {@link #text}
     */
    public String getText() {
        return text;
    }

    /**
     * @return {@link #function}
     */
    public Runnable getFunction() {
        return function;
    }
}
