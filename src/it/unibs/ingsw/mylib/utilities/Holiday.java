package it.unibs.ingsw.mylib.utilities;

/**
 * Enumerativo, serve per svolgere operazioni con i festivi e rappresentarli.
 * @see <a href="https://stackoverflow.com/questions/70734819/how-to-call-getseasonmonths-october/How">Enumerativi per le stagioni</a>
 */
public enum Holiday {
    /**
     * Primo dell'anno.
     */
    FIRST_DAY_OF_YEAR ("01-01"),
    /**
     * San valentino.
     */
    VALENTINES_DAY ("14-02"),
    /**
     * San Faustino.
     */
    SAN_FAUSTINO("15-02"),
    /**
     * Pasqua.
     */
    EASTER ("09-04"),
    /**
     * Pasquetta.
     */
    EASTER_MONDAY ("10-04"),
    /**
     *Primo maggio.
     */
    FIRST_MAY ("01-05"),
    /**
     * Festa della Repubblica.
     */
    REPUBLIC_DAY ("02-06"),
    /**
     * Ferragosto.
     */
    ASSUMPTION("15-08"),
    /**
     * Halloween.
     */
    HALLOWEEN("31-10"),
    /**
     * Primo novembre.
     */
    NOVEMBER_FIRST ("01-11"),
    /**
     * Immacolata.
     */
    IMMACULATE_CONCEPTION("08-12"),
    /**
     * Santa Lucia.
     */
    SANTA_LUCIA("13-12"),
    /**
     * Vigilia di Natale.
     */
    CHRISTMAS_EVE ("24-12"),
    /**
     * Natale.
     */
    CHRISTMAS_DAY("25-12"),
    /**
     * Santo Stefano.
     */
    BOXING_DAY("26-12"),
    /**
     * Capodanno.
     */
    NEW_YEARS_EVE("31-12");

    /**
     * Data in formato GG-MM.
     */
    private String displayName;

    /**
     * Metodo per settare un giorno festivo.
     * @param date il giorno versitivo.
     */
    Holiday(String date) {
        this.displayName = date;
    }

    /**
     * @return {@link #displayName}
     */
    public String getDisplayName() {return this.displayName;}
}