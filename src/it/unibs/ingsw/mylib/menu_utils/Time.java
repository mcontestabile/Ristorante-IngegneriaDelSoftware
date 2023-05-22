package it.unibs.ingsw.mylib.menu_utils;

/**
 * Classe per la gestione di thread che vanno a rallentare
 * a livello grafico il flusso di dati sulla linea di comando.
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 */
public class Time {
    public static final int REALLY_LOW_MILLIS_PAUSE = 250;
    public static final int LOW_MILLIS_PAUSE = 500;
    public static final int MEDIUM_MILLIS_PAUSE = 750;
    public static final int HIGH_MILLIS_PAUSE = 1000;

    /**
     * Metodo che ritarda il thread.
     * @param millisPause il tempo per cui il thread va ritardato.
     */
    public static void pause(int millisPause) {
        try {
            Thread.sleep(millisPause);
        } catch (InterruptedException ignored) {
        }
    }
}
