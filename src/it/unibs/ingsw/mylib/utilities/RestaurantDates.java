package it.unibs.ingsw.mylib.utilities;

import it.unibs.ingsw.users.registered_users.UserController;
import it.unibs.ingsw.users.registered_users.UserDAO;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;

/**
 * Classe di utilità che ha il compito di effettuare i controlli sulle date di {@code Restaurant}.
 *
 * @see <a href="http://www.labsquare.it/?p=4457">Operare con le date in Java</a>
 * @see <a href="https://www.javabrahman.com/java-8/java-8-how-to-get-day-of-week-month-in-spanish-french-for-any-date-using-locale/">Ottenere informazioni da una data usando la classe Locale</a>
 * @see <a href="https://www.geeksforgeeks.org/java-program-format-time-in-mmmm-format/">Formati delle date</a>
 * @see <a href="https://www.baeldung.com/java-get-day-of-week">Ottenere il giorno della settimana da Date</a>
 * @see <a href="https://www.javatpoint.com/java-string-to-date">Convertire String in Date</a>
 * @see <a href="https://www.baeldung.com/java-year-month-day">Recuperare mese, giorno e anno da Date</a>
 * @see <a href="https://stackoverflow.com/questions/51843760/how-can-i-change-the-language-of-the-months-provided-by-localdate">Cambiare la lingua dei mesi forniti da LocalDate</a>
 * @see <a href="http://www.java2s.com/example/java-utility-method/calendar-holiday/isholiday-calendar-cal-39fc6.html">Impostare le festività.</a>
 * @see <a href="https://www.scaler.com/topics/split-in-java/">Split di una stringa in Java.</a>
 */
public class RestaurantDates {
    /**
     * Formatter della data, serve per avere la conversione nel formato italiano.
     */
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /**
     * Data del giorno attuale.
     */
    public static final LocalDate today = LocalDate.now();
    /**
     * Data del giorno successivo.
     */
    public static final LocalDate tomorrow = today.plusDays(1);
    /**
     * Data del giorno lavorativo successivo.
     */
    public static LocalDate workingDay;
    /**
     * Data del giorno lavorativo successivo formattata.
     */
    public static String workingDayString;

    /**
     * Metodo che una data sia nel formato corretto.
     * @param date data inserita.
     * @return se il formato è corretto, ossia dd-MM-yyyy.
     */
    /*
     @ requires date != null;
     */
    public static boolean isValidDate(@NotNull String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    /**
     * Metodo che una data sia nel formato corretto.
     * @param date data inserita.
     * @return se il formato è corretto, ossia un giorno della settimana — «lunedì», «martedì»...
     */
    /*
     @ requires date != null;
     */
    public static boolean isValidDayOfWeek(String date) {
        final DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendOptional(DateTimeFormatter.ofPattern("EEEE")).toFormatter(Locale.ITALY);

        try{
            dtf.parse(date);
        } catch (DateTimeException e) {
            return false;
        }
        return true;
    }

    /**
     * Metodo che una data sia nel formato corretto.
     * @param date data inserita.
     * @return se il formato è corretto, ossia una stagione — «estate», «primavera»...
     */
    /*
     @ requires date != null;
     */
    public static boolean isValidSeason(String date) {
        HashMap<String, Season> seasons = new HashMap<>();
        for (Season s : Season.values())
            seasons.put(s.getDisplayName(), s);

        return seasons.containsKey(date);
    }

    /**
     * Questo metodo controlla che ci sia un match nel periodo che si sta considerando.<br>
     * Infatti, il lavoro del ristorante è quello di impostare il menù alla carta e i menù
     * tematici un giorno prima rispetto al giorno lavorativo — ad esempio, lavoro al menù il
     * martedì per il mercoledì. Inoltre, questo metodo controlla anche che si stia aggiungendo
     * opportunamente un piatto al menù in questione. Ad esempio, se è venerdì e voglio
     * aggiungere un piatto estivo, posso farlo, ma se voglio aggiungere un piatto
     * invernale a luglio no.
     * <br>
     * <br>
     * Il piatto può essere inserito nel menù tematico solo
     * ed esclusivamente se il piatto ha un periodo di
     * validità compatibile con quello del menù tematico.<br>
     * Le combinazioni possibili piatto-menù, in termini di
     * validità, sono le seguenti<p>
     *
     * <table class="Corrispondenza piatto-ricetta"><tbody>
     *      <tr><th>PIATTO</th><th></th><th>MENÙ</th></tr>
     *      <tr><td>tutto l'anno</td><td></td><td>[qualsiasi valore]</td></tr>
     *      <tr><td>[qualsiasi valore]</td><td></td><td>tutto l'anno</td></tr>
     *      <tr><td>valore</td><td></td><td>valore</td></tr>
     *      <tr><td>(giorno)-mese-(anno)</td><td></td><td>stagione</td></tr>
     *      <tr><td>stagione</td><td></td><td>(giorno)-mese-(anno)</td></tr>
     *      <tr><td>giorno-mese-anno</td><td></td><td>giorno settimana</td></tr>
     *      <tr><td>giorno settimana</td><td></td><td>giorno-mese-anno</td></tr>
     *      <tr><td>stagione</td><td></td><td>giorno settimana</td></tr>
     *      <tr><td>giorno settimana</td><td></td><td>stagione</td></tr>
     * </tbody></table>
     *<br>
     *<br>
     * Questo metodo deve controllare che il piatto che si vuole inserire
     * e il menu abbiano una validità nel giorno-mese-anno che si sta facendo
     * funzionare il programma. Infatti, le richieste del dominio applicativo
     * specificano che «Il menu alla carta relativo a una certa data è unico
     * e contiene tutti e soli i piatti che sono disponibili in quella data».
     *<br>
     * @param dishAvailability la disponibilità del piatto che vogliamo nel menu tematico.
     * @param menuAvailability disponibilità prescelta per il menu tematico.
     * @return se il piatto ha una validità compatibile con quella del menù tematico.
     */
    /*
     @ requires dishAvailability != null && menuAvailability != null && tomorrow != null && tomorrowDate != null;
     */
    public static boolean checkPeriod(@NotNull String dishAvailability, @NotNull String menuAvailability, @NotNull String tomorrow, @NotNull LocalDate tomorrowDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DayOfWeek tDay = tomorrowDate.getDayOfWeek();
        Month tMonth = tomorrowDate.getMonth();

        // Obiettivo: controllare che il piatto sia in un periodo di validità che vada bene sia per il menù che per la data in cui sarà disponibile.

        // I caso, se il piatto ha validità tutto l'anno, può essere inserito nel menù qualsiasi giorno sia.
        if(dishAvailability.equalsIgnoreCase("tutto l'anno"))
            return true;

        // II caso, se il menu ha validità tutto l'anno, il piatto non è detto che possa essere inserito in qualsiasi menù.
        if (menuAvailability.equalsIgnoreCase("tutto l'anno")) {
            if (isValidDate(dishAvailability) && dishAvailability.equalsIgnoreCase(tomorrow)) // potrebbero entrambe essere venerdì, 09-03-2023
                return true;
            else if (isValidDayOfWeek(dishAvailability)) { // piatto: lunedì && giorno: 06-03-2023 (lunedì)
                return tDay.getDisplayName(TextStyle.FULL, Locale.ITALY).equalsIgnoreCase(dishAvailability);
            } else if(isValidSeason(dishAvailability)) {
                return Season.from(tMonth.getDisplayName(TextStyle.FULL, Locale.ITALY)).equalsIgnoreCase(dishAvailability);
            }
        }

        // III caso, match delle stringhe potrebbero entrambe essere "02-03-2023", "estate" oppure "lunedì".
        else if (dishAvailability.equalsIgnoreCase(menuAvailability)) {
            return true;
        }

        // IV caso, estrapoliamo il mese e vediamo se fa parte della stagione fornita.
        else if(isValidDate(dishAvailability) && isValidSeason(menuAvailability)) {
            LocalDate ld = LocalDate.parse(dishAvailability, formatter);
            Month month = ld.getMonth();
            return menuAvailability.equalsIgnoreCase(Season.from(month.getDisplayName(TextStyle.FULL, Locale.ITALY)));

            // V caso, dalla data fornita bisogna estrapolare la stagione, e viceversa.
        } else if(isValidDate(menuAvailability) && isValidSeason(dishAvailability)) {
            LocalDate ld = LocalDate.parse(menuAvailability, formatter);
            Month month = ld.getMonth();
            return dishAvailability.equalsIgnoreCase(Season.from(month.getDisplayName(TextStyle.FULL, Locale.ITALY)));

            // VI caso, dal giorno fornito bisogna verificare che il giorno della settimana del menu corrisponda.
        } else if(isValidDate(dishAvailability) && isValidDayOfWeek(menuAvailability)) {
            LocalDate ld = LocalDate.parse(dishAvailability, formatter);
            DayOfWeek dow = ld.getDayOfWeek();
            return menuAvailability.equalsIgnoreCase(dow.getDisplayName(TextStyle.FULL, Locale.ITALY));

            // VII caso, dal giorno fornito bisogna verificare che il giorno della settimana del menu corrisponda.
        } else if(isValidDate(menuAvailability) && isValidDayOfWeek(dishAvailability)) {
            LocalDate ld = LocalDate.parse(menuAvailability, formatter);
            DayOfWeek dow = ld.getDayOfWeek();
            return dishAvailability.equalsIgnoreCase(dow.getDisplayName(TextStyle.FULL, Locale.ITALY));

            // VIII caso, bisogna controllare che, in base al giorno successivo, si abbia match sia con stagione che con giorno della settimana.
        } else if(isValidSeason(dishAvailability) && isValidDayOfWeek(menuAvailability)) {
            return menuAvailability.equalsIgnoreCase(tDay.getDisplayName(TextStyle.FULL, Locale.ITALY)) && dishAvailability.equalsIgnoreCase(Season.from(tMonth.getDisplayName(TextStyle.FULL, Locale.ITALY)));

            // IX caso, bisogna controllare che, in base al giorno successivo, si abbia match sia con stagione che con giorno della settimana.
        } else if(isValidDayOfWeek(dishAvailability) && isValidSeason(menuAvailability)) {
            return dishAvailability.equalsIgnoreCase(tDay.getDisplayName(TextStyle.FULL, Locale.ITALY)) && menuAvailability.equalsIgnoreCase(Season.from(tMonth.getDisplayName(TextStyle.FULL, Locale.ITALY)));
        }

        return false;
    }

    /**
     * Metodo che si occupa di controllare che una disponibilità sia coerente con la data del giorno lavorativo,
     * serve per poter inserire menù/i piatti del menù che siano validi il giorno lavorativo per cui si
     * sta stabilendo il menù.
     * <p>
     * @param availability disponibilità scelta dal gestore.
     * @return se la data è idonea o meno.
     */
    /*
     @ requires availability != null && tomorrow != null && tomorrowDate != null;
     */
    public static boolean checkAvailability(@NotNull String availability) {
        DayOfWeek tDay = workingDay.getDayOfWeek();
        Month tMonth = workingDay.getMonth();

        if(availability.equalsIgnoreCase("tutto l'anno"))
            return true;
        if(RestaurantDates.isValidDate(availability) && workingDayString.equalsIgnoreCase(availability))
            return true;
        else if(RestaurantDates.isValidDayOfWeek(availability)) {
            if(!isHoliday(availability))
                return availability.equalsIgnoreCase(tDay.getDisplayName(TextStyle.FULL, Locale.ITALY));
        } else if(RestaurantDates.isValidSeason(availability)) {
            return availability.equalsIgnoreCase(Season.from(tMonth.getDisplayName(TextStyle.FULL, Locale.ITALY)));
        }

        return false;
    }

    /**
     * Metodo che serve per verificare che la ricetta che si vuole inserire abbia una data valida.
     * <p>
     * @param availability disponibilità scelta dal gestore.
     * @return se la data è idonea o meno.
     */
    /*
     @ requires availability != null;
     */
    public static boolean checkDate(@NotNull String availability) {
        if(availability.equalsIgnoreCase("tutto l'anno"))
            return true;
        if(RestaurantDates.isValidDate(availability) || RestaurantDates.isValidDayOfWeek(availability))
            // il piatto non può essere disponibile un giorno festivo, perché il ristorante lavora solo i feriali.
            return !isHoliday(availability);
        else return RestaurantDates.isValidSeason(availability);
    }

    /**
     * Metodo che serve per verificare il giorno lavorativo successivo non sia un festivo.
     * <p>
     * @param tomorrowDate data del giorno lavorativo successivo.
     * @return se la data è idonea o meno.
     */
    /*
     @ requires tomorrowDate != null;
     */
    public static boolean checkDate(@NotNull LocalDate tomorrowDate) {
        DayOfWeek tDay = tomorrowDate.getDayOfWeek();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String tomorrowString = tomorrowDate.format(formatter); // Data del giorno successivo formattata.
        String tDayItalian = tDay.getDisplayName(TextStyle.FULL, Locale.ITALY);
        return !isHoliday(tDayItalian) && !isHoliday(tomorrowString);
    }

    /**
     * Controlla che la stringa immessa non sia un festivo.
     *
     * @param availability disponibilità del piatto.
     * @return se è un giorno festivo o meno.
     */
    private static boolean isHoliday(@NotNull String availability) {
        if (availability.equalsIgnoreCase("sabato") || availability.equalsIgnoreCase("domenica"))
            return true;
        else if(availability.equalsIgnoreCase("lunedì") || availability.equalsIgnoreCase("martedì") ||
                availability.equalsIgnoreCase("mercoledì") || availability.equalsIgnoreCase("giovedì") ||
                availability.equalsIgnoreCase("venerdì"))
            return false;

        HashMap<String, Holiday> holidays = new HashMap<>();
        for (Holiday h : Holiday.values())
            holidays.put(h.getDisplayName(), h);

        String[] result = availability.split("-", 0); // splittando la data con "-".
        String dayMonth = result[0] + "-" + result[1];

        return holidays.containsKey(dayMonth);
    }

    /**
     * Controlla che la data immessa non sia un festivo.
     *
     * @param date data di domani.
     * @return se è un giorno festivo o meno.
     */
    public static boolean isHoliday(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String ld = date.format(formatter);
        String tDay = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALY);

        if (tDay.equalsIgnoreCase("sabato") || tDay.equalsIgnoreCase("domenica"))
            return true;

        HashMap<String, Holiday> holidays = new HashMap<>();
        for (Holiday h : Holiday.values())
            holidays.put(h.getDisplayName(), h);

        String[] result = ld.split("-", 0); // splittando la data con "-".
        String dayMonth = result[0] + "-" + result[1];

        return holidays.containsKey(dayMonth);
    }

    public static void setWorkingDay() {
        /*
         * Una volta confermato che il programma è stato avviato in un giorno in cui il
         * ristorante è operativo, è il momento di controllare se l'indomani è un festivo.
         * Se oggi è venerdì/prefestivo, si setta il ristorante per IL GIORNO LAVORATIVO SUCCESSIVO.
         * Quindi, bisogna controllare che giorno è e impostare workingDay di conseguenza.
         */
        if(!RestaurantDates.isHoliday(tomorrow)) {
            // Se non è un festivo, si può settare senza problemi workingDay.
            workingDay = today.plusDays(1);
        } else {
            /*
             * Se è un festivo, dalla data dobbiamo partire dal presupposto di aggiungere almeno
             * due giorni alla data odierna, per andare a dopodomani, che, potenzialmente, non
             * dovrebbe essere un festivo. Sarà il metodo a controllare che giorno lavorativo servirà.
             */
            workingDay = RestaurantDates.setWorkingDay(today.plusDays(2));
        }

        workingDayString = workingDay.format(formatter);
    }

    /**
     * Setta la data del giorno lavorativo successivo, sulla
     * base di un potenziale giorno lavorativo da verificare.
     * @return il giorno lavorativo effettivo.
     */
    public static LocalDate setWorkingDay(LocalDate date) {
        String tDay = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALY);

        if(isHoliday(date))
            // Se è un festivo, iteriamo il metodo sul giorno successivo. Esempio: 14-02 festivo, date = 15-02, ma anch'esso è festivo.
            return setWorkingDay(date.plusDays(1));
        // Esempio: oggi è venerdì, il giorno lavorativo successivo è lunedì.
        else if(tDay.equalsIgnoreCase("venerdì")) {
            LocalDate possibleWorkingDay = date.plusDays(3); // bisogna impostare come workingDay lunedì.
            // Ma bisogna anche controllare che il lunedì scelto non sia un festivo!
            if (!isHoliday(possibleWorkingDay))
                return possibleWorkingDay; // Deve essere lunedì.
            else
                return setWorkingDay(possibleWorkingDay.plusDays(2)); // si controlla che il giorno dopo il festivo non sia un festivo.
        }

        return date; // se è un giorno fra lunedì e giovedì e non c'è di mezzo un festivo, il giorno lavorativo è quello successivo.
    }
}