package it.unibs.ingsw.mylib.menu_utils;

import it.unibs.ingsw.mylib.utilities.DataInput;
import java.util.TreeMap;

/**
 * Console customizzabile per {@code Handler}.
 * <br>
 * Il computer ha diversi thread che lavorano (in certi casi, dipende
 * dal programma che stiamo eseguendo) in contemporanea. Noi usiamo runnable
 * perché in futuro, quando useremo i thread, un thread separato, rispetto a
 * quello che usiamo, esegue il menu. Ci permette di fare due operazioni in contemporanea
 * (ad esempio, il menu e un'altra funzionalità del programma).
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 * <br>
 * <br>
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub></a>
 */

public class Menu implements Runnable {
    private String title;
    private TreeMap<String, MenuItem> itemsMap;
    private String endKey = "0";
    private int frameLength;
    private int enterRepetitions = 3;
    private String exit = "EXIT";

    /**
     * Costruisce un nuovo {@code Handler} avente come valori gli {@code Items}.
     *
     * @param items gli item del menu.
     */
    public Menu(String title, MenuItem[] items) {
        this.title = title;
        itemsMap = new TreeMap<>();
        for (int i=0; i<items.length; i++) {
            itemsMap.put((i+1)+"", items[i]);
        }
        frameLength = 0;
    }

    /**
     * Inizia l'esecuzione di {@code Handler}.
     */
    @Override
    public void run() {
        generateFrameLength();
        String frame = MenuUtils.generateFrame(frameLength);

        String choice;
        do {
            System.out.println(MenuUtils.formatTitle(title, frame));
            printOptions();
            System.out.println(frame);
            choice = DataInput.readNotEmptyString("» ");
            if (itemsMap.containsKey(choice))
                itemsMap.get(choice).getFunction().run();
            else if (!choice.equals(endKey)) {

                System.out.print(MenuUtils.INVALID_OPTION_A);
                Time.pause(Time.LOW_MILLIS_PAUSE);
                MenuUtils.spellingPrint(MenuUtils.INVALID_OPTION_B);
                Time.pause(Time.LOW_MILLIS_PAUSE);

            }
            System.out.println(MenuUtils.getEnters(enterRepetitions));
        } while (!choice.equals(endKey));
    }

    /**
     * Stampa i valori di {@link #itemsMap}, che rappresentano le opzioni del menu.
     */
    private void printOptions() {
        for (String key : itemsMap.keySet()) {
            System.out.println("[" + key + "] " + itemsMap.get(key).getText());
        }
        System.out.println("[" + endKey + "] " + exit + "\n");
    }

    /**
     * Permette di cambiare il valore di keyValue. Ritorna {@code true} se {@link #itemsMap}
     * non contiene una chiave con lo stesso valore di oldKey e la rimpiazza.
     *
     * @param oldKey chiave del vecchio valore da rimpiazzare.
     * @param newKey nuovo valore della chiave.
     * @return {@code true} se endKey è stato sostituito;
     *         {@code false} altrimenti
     */
    public boolean changeChoiceKey(String oldKey, String newKey) {
        if (!itemsMap.containsKey(oldKey) || itemsMap.containsKey(newKey) || newKey.equals(endKey))
            return false;
        itemsMap.put(newKey, itemsMap.remove(oldKey));
        return true;
    }

    /**
     * Permette di cambiare il valore di {@link #endKey}.
     * <br>
     * Ritorna {@code true} se {@link #itemsMap}
     * non contiene una chiave uguale a newEndKey.
     *
     * @param newEndKey il nuovo valore di endKey
     * @return {@code true} se endKey è stato sostituito;
     *         {@code false} altrimenti.
     */
    public boolean changeEndKey(String newEndKey) {
        if (itemsMap.containsKey(newEndKey))
            return false;
        endKey = newEndKey;
        return true;
    }

    /**
     * Permette di cambiare il valore di {@link #enterRepetitions}.
     *
     * @param enterRepetitions nuovo numero per {@link #enterRepetitions}.
     * @return {@code true} if enterRepetitions è stato sostituito;
     *         {@code false} altrimenti.
     */
    public boolean changeEnterRepetitions(int enterRepetitions) {
        if (enterRepetitions < 0) return false;
        this.enterRepetitions = enterRepetitions;
        return true;
    }

    /**
     * Permette di cambiare il valore di {@link #enterRepetitions}.
     *
     * @param text il nuovo testo di {@link #exit}.
     */
    public void changeExitText(String text) {
        exit = text;
        generateFrameLength();
    }

    /**
     * Genera la lunghezza della cornice {@link #frameLength}.
     */
    private void generateFrameLength() {
        int t;
        for (String key : itemsMap.keySet()) {
            t = (itemsMap.get(key).getText().length() + 4);
            if (t > frameLength)
                frameLength = t;
        }
        if (title.length()+4 > frameLength)
            frameLength = title.length()+4;

        if (exit.length()+3 > frameLength)
            frameLength = exit.length()+3;
    }
}
