package it.unibs.ingsw.mylib.xml_utils;

/**
 * Classe che si occupa di fare il "mapping" fra il tag e il valore.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 */
public class XMLAttribute {
    private String name;
    private String value;

    /**
     * Costruttore
     * @param name nome del tag.
     * @param value valore contenuto nel tag.
     */
    public XMLAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Getter, ritorna il nome del tag,
     *
     * @return il nome del tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter, ritorna il valore del tag.
     *
     * @return il valore del tag.
     */
    public String getValue() {
        return value;
    }
}
