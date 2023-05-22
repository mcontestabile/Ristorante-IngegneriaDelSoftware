package it.unibs.ingsw.mylib.menu_utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * {@code MenuUtils} contiene stringhe di utilità per la classe {@code Handler}.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub></a>
 * @see Menu
 */
public class MenuUtils {
    public static final String ENTER = "\n↩";
    public static final String LEFT_UP = "\n╔";
    public static final String VERTICAL_LEFT = "\n║";
    public static final String VERTICAL_RIGHT = "║";
    public static final String RIGHT_UP = "╗";
    public static final String RIGHT_DOWN = "╝\n";
    public static final String FRAME_SYMBOL = "═";
    public static final String LEFT_DOWN = "\n╚";
    public static final String INVALID_OPTION = "‼ Scelta invalida\n";
    public static final String INVALID_OPTION_A = "‼ Scelta ‼";
    public static final String INVALID_OPTION_B = " invalida\n";


    /**
     * Genera il cornice del menu.
     *
     * @param frameLength lunghezza della cornice.
     * @return la cornice in formato {@code String}.
     */
    @Contract(pure = true)
    public static @NotNull String generateFrame(int frameLength) {
        return FRAME_SYMBOL.repeat(frameLength);
    }

    /**
     * Metodo che formatta il titolo.
     *
     * @param title il titolo del menu.
     * @param frame la cornice del menu.
     * @return il titolo formattato.
     */
    public static @NotNull String formatTitle(@NotNull String title, @NotNull String frame) {
        String formattedTitle = "";
        String tempTitle = ""; //variabile temporanea su cui salvare il titolo
        double spaces = (frame.length()-2-title.length())/2.;
        tempTitle += " ".repeat((int)Math.floor(spaces));
        tempTitle += title;
        tempTitle += " ".repeat((int)Math.ceil(spaces));
        formattedTitle += LEFT_UP + frame.substring(2) + RIGHT_UP;
        formattedTitle += VERTICAL_LEFT + tempTitle + VERTICAL_RIGHT;
        formattedTitle += LEFT_DOWN + frame.substring(2) + RIGHT_DOWN;
        return formattedTitle;
    }

    /**
     * Ritorna gli enters in formato {@code String}.
     */
    @Contract(pure = true)
    public static @NotNull String getEnters(int repetitions) {return MenuUtils.ENTER.repeat(repetitions);}

    /**
     * Stampa {@code text} con spelling, non mostrando
     * nell'immediato tutto ciò che l'utente deve leggere.
     *
     * @param text testo da stampare a video.
     */
    public static void spellingPrint(@NotNull String text) {
        for (char s : text.toCharArray()) {
            Time.pause(Time.REALLY_LOW_MILLIS_PAUSE);
            System.out.print(s);
        }
    }
}
