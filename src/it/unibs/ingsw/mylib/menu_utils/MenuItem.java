package it.unibs.ingsw.mylib.menu_utils;

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
public class MenuItem {
    private String text;
    private Runnable function; // Il menu utente è gestito da un thread.

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
