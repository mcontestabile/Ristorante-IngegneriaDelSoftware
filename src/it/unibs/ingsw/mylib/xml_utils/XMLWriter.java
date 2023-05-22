package it.unibs.ingsw.mylib.xml_utils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Questa classe si occupa della scrittura di un file .xml.
 *
 * @author TheTrinity - Progetto Arnaldo 2021
 * @author Baresi Marco
 * @author Contestabile Martina
 * @author Iannella Simone
 *
 * @see <a href="https://github.com/GithubboAncheIo/TheTrinity_Arnaldo2021">TheTrinity GitHub</a>
 * @see XMLStreamWriter
 */
public class XMLWriter {
    private XMLStreamWriter xmlWriter = null;
    public final static String ENCODING = "utf-8";
    public final static String VERSION = "1.0";
    public static int tabLevel = 0;
    public static final int INCREMENT_LEVEL = 1;
    public static final int SAME_LEVEL = 0;
    public static final int DECREMENT_LEVEL = -1;

    /**
     * Classe che genera un file .xml che ha un nome che è una data Stringa,
     * il cui nome è dato dall'utente oppure stabilito dal programmatore a priori.
     *
     * @param fileName contiene il nome del file di output.
     */
    public XMLWriter(String fileName) {
        try {
            /*
             * Parte di codice che si occupa di creare ed istanziare xmlReader,
             * che sarà utilizzata per scrivere il file .xml.
             */
            XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();
            xmlWriter = xmlFactory.createXMLStreamWriter(new FileOutputStream(fileName), ENCODING);
            xmlWriter.writeStartDocument(ENCODING, VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scrive il tag di apertura in fileName.xml.
     *
     * @param openingTag nome del tag di aoertura.
     */
    public void writeOpeningTagXML(String openingTag) {
        try {
            // Inizialmente, dobbiamo indentare il file, se vogliamo una scrittura ordinata.
            writeTabs(INCREMENT_LEVEL);
            xmlWriter.writeStartElement(openingTag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo chiude un tag di apertura, cioè
     * scrive il tag di chiusura.
     *
     * @param closeDocument se vero, chiude il documento.
     */
    public void writeClosingTagXML(boolean closeDocument) {
        try {
            // Il tag di chiusura deve essere allineato a quello di apertura.
            writeTabs(DECREMENT_LEVEL);
            xmlWriter.writeEndElement();

            // Determina se la scrittura del file è terminata o meno e, di conseguenza, determina come agire.
            if (closeDocument) {
                xmlWriter.writeEndDocument();
                xmlWriter.flush();
                xmlWriter.close();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo scrive l'oggetto che ci serve salvare nel file .xml.
     *
     * @param obj rappresenta un generico oggetto, ma la sua classe deve estendere l'interfaccia Writable.
     * @param <T> rappresenta un tipo generico, ma deve necessariamente estendere l'interfaccia Writable.
     */
    public <T extends Writable> void writeObjectXML(T obj) {
        // Elementi necessari per la scrittura corretta del file .xml.
        ArrayList<XMLTag> elements;
        ArrayList<XMLAttribute> attributes;
        String startTagName;
        boolean emptyElement = false;
        boolean emptyTag;
        boolean singleAttribute = false;

        // Blocco try-catch per gestire le possibili eccezioni che possono accadere.
        try {
            // Otteniamo le informazioni che l'oggetto possiede
            startTagName = obj.getTagName();
            attributes = obj.getAttributesToWrite();
            elements = obj.getChildTagsToWrite();

            // Indentiamo la scrittura del nodo figlio, sempre per dare quell'aspetto gerarchico tipico di .xml.
            writeTabs(INCREMENT_LEVEL);

            // Determinamio il tag da scrivere e i suoi attributi.
            XMLTag tag = new XMLTag(startTagName, attributes.toArray(new XMLAttribute[0]));
            if (elements == null || elements.size() == 0) emptyElement = true; // Se il tag è privo di elementi, ovviamente non dobbiamo scrivergli nulla all'interno.
            writeTag(tag, emptyElement);

            // Se l'oggetto ha degli attributi da scrivere, allora bisogna capire quanti sono e come scriverli.
            if (elements != null) {
                for (XMLTag t : elements) {
                    emptyTag = false;
                    // Serie di if per determinare il numero di attributi dell'oggetto.
                    if (t.getTagName().equals(startTagName) && elements.size() == 1)
                        singleAttribute = true;
                    if (t.getTagValue() == null)
                        emptyTag = true;
                    if (!singleAttribute) {
                        writeTabs(SAME_LEVEL); // Ovviamente, gli attributi vanno scritti nello stesso livello di indentazione.
                        writeTag(t, emptyTag);
                    }
                    xmlWriter.writeCharacters(t.getTagValue());
                    if (!singleAttribute && !emptyTag) xmlWriter.writeEndElement();
                }
            }
            if (!singleAttribute) writeTabs(DECREMENT_LEVEL); // Terminata la scrittura, decrementiamo il livello di indentazione.
            else tabLevel--;
            if (!emptyElement) xmlWriter.writeEndElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo scrive l'ArrayList che vogliamo salvare nel file .xml fileName.xml,
     * considerando i parametri che desideriamo scrivere e che inseriamo nell'ArrayList.
     *
     * @param objList        è un generico oggetto di tipo ArrayList.
     * @param arrayName      è il tag dell'array.
     * @param arrayAttribute sono gli attributi dell'array.
     * @param attributeValue sono i valori degli attributi.
     * @param openingTag     tag di apertura del documento.
     * @param <T>            tipo generico, deve implementare Writable.
     */
    public <T extends Writable> void writeArrayListXML(ArrayList<T> objList, String arrayName, String arrayAttribute, String attributeValue, String openingTag) {
        try {
            if (openingTag != null) writeOpeningTagXML(openingTag); // Scrivo il tag di apertura.
            if (arrayName != null) { // Iniziamo la scrittura una volta che la condizione è verificata.
                writeTabs(INCREMENT_LEVEL);
                xmlWriter.writeStartElement(arrayName);
                if (arrayAttribute != null) xmlWriter.writeAttribute(arrayAttribute, attributeValue);
            }

            for (T obj : objList)
                writeObjectXML(obj);

            if (arrayName != null) {
                writeTabs(DECREMENT_LEVEL);
                xmlWriter.writeEndElement(); // Terminamio la scrittura una volta che la condizione è verificata.
            }
            if (openingTag != null) writeClosingTagXML(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo scrive l'ArrayList che vogliamo salvare nel file .xml fileName.xml,
     * considerando i parametri che desideriamo scrivere e che inseriamo nell'ArrayList.
     *
     * @param objList        è un generico oggetto di tipo ArrayList.
     * @param arrayName      è il tag dell'array.
     * @param arrayAttribute sono gli attributi dell'array.
     * @param attributeValue sono i valori degli attributi.
     */
    public <T extends Writable> void writeArrayListXML(ArrayList<T> objList, String arrayName, String arrayAttribute, String attributeValue) {
        writeArrayListXML(objList, arrayName, arrayAttribute, attributeValue, null);
    }

    /**
     * Questo metodo scrive l'ArrayList che vogliamo salvare nel file .xml fileName.xml,
     * considerando i parametri che desideriamo scrivere e che inseriamo nell'ArrayList.
     *
     * @param objList        è un generico oggetto di tipo ArrayList.
     * @param arrayName      è il tag dell'array.
     */
    public <T extends Writable> void writeArrayListXML(ArrayList<T> objList, String arrayName) {
        writeArrayListXML(objList, arrayName, null, null, null);
    }

    /**
     * Questo metodo scrive l'ArrayList che vogliamo salvare nel file .xml fileName.xml,
     * considerando i parametri che desideriamo scrivere e che inseriamo nell'ArrayList.
     *
     * @param objList        è un generico oggetto di tipo ArrayList.
     */
    public <T extends Writable> void writeArrayListXML(ArrayList<T> objList) {
        writeArrayListXML(objList, null, null, null);
    }

    /**
     * Metodo che si occupa della scrittura dei tag.
     *
     * @param t tag
     * @param emptyTag variabile booleana che informa se il tag è vuoto o meno.
     * @throws XMLStreamException gestito da altri metodi.
     */
    private void writeTag(XMLTag t, boolean emptyTag) throws XMLStreamException {
        if (emptyTag)
            xmlWriter.writeEmptyElement(t.getTagName());
        else
            xmlWriter.writeStartElement(t.getTagName());

        for (XMLAttribute a : t.getAttributes())
            xmlWriter.writeAttribute(a.getName(), a.getValue());
    }

    /**
     * @param changeLevel se 1, aumenta tabLevel (il numero dei tab)
     *                    se -1, diminuisce tabLevel
     * @throws XMLStreamException gestito da altri metodi.
     */
    private void writeTabs(int changeLevel) throws XMLStreamException {
        if (changeLevel == DECREMENT_LEVEL) tabLevel--;
        xmlWriter.writeCharacters("\n");
        for (int i = 0; i < tabLevel; i++) {
            xmlWriter.writeCharacters("\t");
        }
        if (changeLevel == INCREMENT_LEVEL) tabLevel++;
    }
}