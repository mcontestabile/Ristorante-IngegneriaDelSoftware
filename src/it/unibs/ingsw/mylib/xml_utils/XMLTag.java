package it.unibs.ingsw.mylib.xml_utils;

/**
 * Questa è la classe XMLTag. Abbiamo dovuto crearla in modo tale
 * da costruire i tags che sono usati nelle interfacce Parsable e
 * Writable. Questa Classe è utile in quanto, mentre dobbiamo scrivere
 * il XML di output e concentrarci sul task principale di questo programma,
 * cioè la gestione del ristorante e l'utilizzo degli xml come database,
 * rudimentale, ci servono sempre i tags, che dividono ogni singola
 * parte dei dati contenenti nei file .xml.
 *
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 */
public class XMLTag {
    private String tagName;
    private String tagValue;
    private XMLAttribute[] attributes;


    /**
     * Primo tipo di costruttore, permette la creazione di un oggetto XMLTag.
     * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/language/varargs.html">Vargars in Java> per il significato dei ...</a>
     *
     * @param tagName tag.
     * @param tagValue valore del tag.
     * @param attributes attributi, in numero variabile, per questo usiamo i vargars.
     */
    public XMLTag(String tagName, String tagValue, XMLAttribute... attributes) {
        this.tagName = tagName;
        this.tagValue = tagValue;
        this.attributes = attributes;
    }

    /**
     * Secondo tipo di costruttore, permette la creazione di un oggetto XMLTag..
     *  @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/language/varargs.html">Vargars in Java> per il significato dei ...</a>
     *
     * @param tagName tag.
     * @param attributes attributi, in numero variabile, per questo usiamo i vargars.
     */
    public XMLTag(String tagName, XMLAttribute... attributes) {
        this(tagName, null, attributes);
    }

    /**
     * Terzo tipo di costruttore, permette la creazione di un oggetto XMLTag.
     *
     * @param tagName  tag.
     * @param tagValue valore del tag.
     */
    public XMLTag(String tagName, String tagValue) {
        this(tagName, tagValue, new XMLAttribute[0]);
    }

    /**
     * Quarto tipo di costruttore, permette la creazione di un oggetto XMLTag.
     *
     * @param tagName  tag.
     */
    public XMLTag(String tagName) {
        this(tagName, (String) null);
    }

    /**
     * Metodo che ritorna il nome del tag.
     * @return una stringa che è {@link #tagName}.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Metodo per settare il nome del tag.
     * @param tagName, inizializzazione.
     */
    public void setTagName(String tagName) {this.tagName = tagName;}

    /**
     * Metodo che ritorna il valore del tag.
     * @return una stringa che è {@link #tagValue}.
     */
    public String getTagValue() {
        return tagValue;
    }

    /**
     * Metodo per settare il valore del tag.
     * @param tagValue, inizializzazione.
     */
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    /**
     * Metodo che ritorna il valore degli attributi.
     * @return un array {@link #attributes}
     */
    public XMLAttribute[] getAttributes() {
        return attributes;
    }
}