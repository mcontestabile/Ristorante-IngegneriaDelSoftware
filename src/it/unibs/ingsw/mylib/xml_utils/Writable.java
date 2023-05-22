package it.unibs.ingsw.mylib.xml_utils;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Questa interfaccia va implementata in oggetti che vanno scritti in un file .xml.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 */
public interface Writable {
    MultiMap<String, Supplier<String>> getters = new MultiMap<>();

    /**
     * Metodo che indica come settare gli attributi nel file .xml.
     *
     * @return i tags che vogliamo nel file di output.
     */
    default ArrayList<XMLAttribute> getAttributesToWrite() {
        setGetters();
        ArrayList<XMLAttribute> attributes = new ArrayList<>();
        for (String name : getAttributeStrings()) {
            for (Supplier<String> getter : getters.get(name))
                attributes.add(new XMLAttribute(name, getter.get()));
        }
        return attributes;
    }

    /**
     * Metodo che imposta i tag figli nel file .xml.
     *
     * @return i tag figli che vogliamo nel file di output.
     */
    default ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();
        for (String name : getChildTagStrings()) {
            for (Supplier<String> getter : getters.get(name))
                XMLTags.add(new XMLTag(name, getter.get()));
        }
        return XMLTags;
    }

    /**
     * Metodo che serve per settare i valori dei singoli tag del genere alimentare (extra).
     */
    void setGetters();

    /**
     * Getter per il tag.
     * @return il tag.
     */
    String getTagName();

    /**
     * Getter per gli attributi, sotto forma di stringa.
     *
     * @return gli attributi in un array di stringhe.
     */
    String[] getAttributeStrings();

    /**
     * Getter per ritornare i tag figli.
     *
     * @return i tag figli.
     */
    String[] getChildTagStrings();
}