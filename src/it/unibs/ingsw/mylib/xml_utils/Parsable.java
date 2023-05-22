package it.unibs.ingsw.mylib.xml_utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Questa Interface va implementata in oggetti che devono essere popolati
 * con informazioni contenute in un file .xml.
 *
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 */
public interface Parsable {
    HashMap<String, Consumer<String>> setters = new HashMap<>();

    /**
     * Questo metodo preleva attributi e tag dal file .xml in input.
     *
     * @param xmlTag rappresenta il tag di un file .xml.
     */
    default void setAttribute(@NotNull XMLTag xmlTag) {
        setSetters();
        Consumer<String> method = setters.get(xmlTag.getTagName());
        if (method != null) method.accept(xmlTag.getTagValue());
        XMLAttribute[] attributes = xmlTag.getAttributes();
        if (attributes != null) {
            for (XMLAttribute a : attributes) {
                method = setters.get(a.getName());
                if (method != null) method.accept(a.getValue());
            }
        }
    }

    /**
     * Questo metodo informa se {@link #setters} contiene un specifico tag.
     *
     * @param tag rappresenta il tag di un file .xml.
     * @return se c'è un setter associato al nome del tag oppure no.
     */
    default boolean containsAttribute(String tag) {
        return setters.get(tag) != null;
    }

    /**
     * Il metodo necessita di definire i {@link #setters} usati per settare gli
     * attributi letti dal parser.
     * <p>
     * Esempio di implementazione del metodo:
     * public void setSetters() {
     * setters.put(ATTRIBUTE_STRINGS.get(0), this::setAttribute0);
     * ...
     * }
     */
    void setSetters();

    /**
     * Ritorna, sotto formato di stringa, il valore del primo tag.
     */
    String getStartString();
}