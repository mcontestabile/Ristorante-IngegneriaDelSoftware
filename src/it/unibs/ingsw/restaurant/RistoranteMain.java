package it.unibs.ingsw.restaurant;

import javax.xml.stream.XMLStreamException;
import java.text.ParseException;

/**
 * {@code Main class} del programma {@code Ristorante}.
 * Per lasciare il main « pulito », si è scelto di lasciare
 * tutti i metodi di interazione nella classe {@code Handler}.
 */
public class RistoranteMain {
    public static void main(String[] args) throws XMLStreamException {
        Handler handler = new Handler();
        handler.welcomeMessage();
    }
}
