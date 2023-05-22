package it.unibs.ingsw.mylib.xml_utils;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Questa classe si occupa del processo di parsing del file immesso.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 * @see XMLStreamReader
 */
public class XMLParser {
    private XMLStreamReader xmlReader = null;

    /**
     * Questo metodo recupera il file .xml, ossia fileName, che dobbiamo parsare.
     *
     * @param fileName nome del file xml.
     */
    public XMLParser(String fileName) {
        /*
         * Parte di codice che si occupa di creare ed istanziare xmlReader,
         * che sarà utilizzata per leggere il file .xml.
         */
        try {
            XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
            xmlReader = xmlFactory.createXMLStreamReader(fileName, new FileInputStream(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che esegue il parsing.
     *
     * @param obj oggetto generico.
     * @param <T> tipo generico.
     * @return objList la lista parsata, ossia i dati che sono nel .xml vengono inseriti in un array.
     * @throws XMLStreamException eccezione lanciata nel caso di problemi col parsing.
     */
    public <T extends Parsable> ArrayList<T> parseXML(@NotNull Class<T> obj) throws XMLStreamException {
        String elementName = null;
        XMLTag XMLTag;
        ArrayList<T> objList = new ArrayList<>();
        T t = null;

        /*
         * Noi creiamo una nuova istanza per inizializzare l'oggetto obj,
         * così possiamo usare i setter di T e i controlli di Parsable, e
         * consideriamo anche le eccezioni che possono accadere.
         */
        try {
            t = obj.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /*
         * Mentre hasNext() è true, cioè gli eventi di parsing
         * sono possibili, parsiamo il file.
         */
        while (xmlReader.hasNext()) {
            assert t != null;
            /*
             * abbiamo fatto uno switch, con xmlReader.getEventType()
             * ritorniamo un int che rappresenta l'evento con cui
             * dobbiamo lavorare.
             */
            switch (xmlReader.getEventType()) {
                /* XMLStreamConstants.START_DOCUMENT = 7, stiamo iniziando il parsing. */
                case XMLStreamConstants.START_DOCUMENT -> {
                }

                /* XMLStreamConstants.START_ELEMENT = 1, leggiamo il tag di apertura. */
                case XMLStreamConstants.START_ELEMENT -> {
                    elementName = t.containsAttribute(xmlReader.getLocalName()) ? xmlReader.getLocalName() : null;

                    /* Contiamo gli attributi e li prendiamo, salvandoli. */
                    for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                        String name = xmlReader.getAttributeLocalName(i);
                        String value = xmlReader.getAttributeValue(i);
                        XMLTag = elementName != null ? new XMLTag(elementName, new XMLAttribute(name, value)) : new XMLTag(name, value);
                        t.setAttribute(XMLTag);
                    }
                }

                /* XMLStreamConstants.END_ELEMENT = 2, leggiamo un tag di chiusura. */
                case XMLStreamConstants.END_ELEMENT -> {
                    if (t.getStartString().equals(xmlReader.getLocalName())) {
                        objList.add(t);
                        try {
                            t = obj.getDeclaredConstructor().newInstance();
                        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                /* XMLStreamConstants.COMMENT = 5, leggiamo un commento. Nei nostri file non sono presenti, quindi non facciamo niente, ma manteniamo aperta la possibilità di gestione in futuro. */
                case XMLStreamConstants.COMMENT -> {
                }

                /* XMLStreamConstants.CHARACTERS  = 4, leggiamo del testo dentro un elemento. */
                case XMLStreamConstants.CHARACTERS -> {
                    if (xmlReader.getText().trim().length() > 0 && elementName != null) {
                        XMLTag = new XMLTag(elementName, xmlReader.getText());
                        t.setAttribute(XMLTag);
                    }
                }
            }

            /* Passa all'evento successivo, ritornando un intero identificante il tipo di intero.*/
            xmlReader.next();
        }
        return objList;
    }
}