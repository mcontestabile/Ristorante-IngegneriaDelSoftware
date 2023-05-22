package it.unibs.ingsw.mylib.utilities;

import org.jetbrains.annotations.NotNull;

import java.time.Month;
import java.util.*;

/**
 * Enumerativo, serve per svolgere operazioni con le stagioni e rappresentarle.
 * @see <a href="https://stackoverflow.com/questions/70734819/how-to-call-getseasonmonths-october/How">Enumerativi per le stagioni</a>
 */
public enum Season {
    SPRING ("primavera"),
    SUMMER ("estate"),
    WINTER ("inverno"),
    AUTUMN ("autunno");


    private String displayName;

    /**
     * Costruttore, permette di creare una stagione.
     * @param displayName
     */
    Season (String displayName) {
        this.displayName = displayName;
    }

    /**
     * Metodo che ritorna il nome di una stagione.
     * @return {@link #displayName}
     */
    public String getDisplayName() {return this.displayName;}

    /**
     * Metodo che ritorna la stagione a partire dal mese fornito.
     * @param month il mese fornito.
     * @return la stagione
     */
    public static String from(@NotNull String month) {
        if(month.equalsIgnoreCase("SETTEMBRE") || month.equalsIgnoreCase("OTTOBRE") || month.equalsIgnoreCase("NOVEMBRE"))
            return Season.AUTUMN.displayName;
        else if(month.equalsIgnoreCase("DICEMBRE") || month.equalsIgnoreCase("GENNAIO") || month.equalsIgnoreCase("FEBBRAIO"))
            return Season.WINTER.displayName;
        else if(month.equalsIgnoreCase("GIUGNO") || month.equalsIgnoreCase("LUGLIO") || month.equalsIgnoreCase("AGOSTO"))
            return Season.SUMMER.displayName;
        else if(month.equalsIgnoreCase("MARZO") || month.equalsIgnoreCase("APRILE") || month.equalsIgnoreCase("MAGGIO"))
            return Season.SPRING.displayName;

        return "nessuna corrispondenza";
    }
}