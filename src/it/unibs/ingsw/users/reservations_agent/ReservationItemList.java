package it.unibs.ingsw.users.reservations_agent;

import it.unibs.ingsw.mylib.xml_utils.Writable;
import it.unibs.ingsw.mylib.xml_utils.XMLAttribute;
import it.unibs.ingsw.mylib.xml_utils.XMLTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReservationItemList extends ReservationDecorator implements Writable {
    private HashMap<String, String> item_list;

    public static final String START_STRING = "reservation";
    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();
    public ReservationItemList(Reservable decoratedReservation, HashMap<String, String> itemList) {
        super(decoratedReservation);
        item_list = itemList;
    }
    
    /*
     * La keyword static è usata per creare metodi che esistono indipendentemente
     * da qualsiasi istanza creata per la classe. I metodi statici non usano
     * variabili di istanza di alcun oggetto della classe in cui sono definiti.
     */
    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("resCover");
    }

    /**
     * Metodo che serve per settare i valori dei singoli tag della prenotazione, in particolar modo il nome il numero coperti.
     */
    @Override
    public void setGetters() {
        getters.clear();
        getters.put(ATTRIBUTE_STRINGS.get(0), this::getName);
        getters.put(ATTRIBUTE_STRINGS.get(1), this::getResCover);
    }

    /**
     * Metodo che ritorna il tag di apertura.
     * @return {@link #START_STRING} il tag di apertura.
     */
    @Override
    public String getTagName() {
        return START_STRING;
    }

    /**
     * Metodo che ritorna i tag.
     * @return i tag.
     */
    @Override
    public String[] getAttributeStrings() {
        return ATTRIBUTE_STRINGS.toArray(new String[0]);
    }

    /**
     * Metodo che ritorna i tag come stringhe.
     * @return i tag.
     */
    @Override
    public String[] getChildTagStrings() {
        return new String[]{ReservationItemList.START_STRING};
    }



    /**
     * Metodo per decretare quali nodi figli scrivere.
     * @return i nodi figli.
     */
    @Override
    public ArrayList<XMLTag> getChildTagsToWrite() {
        setGetters();
        ArrayList<XMLTag> XMLTags = new ArrayList<>();


        for(Map.Entry m : item_list.entrySet()) {
            XMLTags.add(new XMLTag("item", new XMLAttribute("item_name", (String)m.getKey()), new XMLAttribute("item_cover", (String)m.getValue())));
        }

        return XMLTags;
    }
}
