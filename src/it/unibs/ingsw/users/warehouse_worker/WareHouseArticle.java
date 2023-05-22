package it.unibs.ingsw.users.warehouse_worker;

import it.unibs.ingsw.mylib.xml_utils.Parsable;

import java.util.ArrayList;

public class WareHouseArticle implements Parsable {

    private String name;
    private double quantity;
    private String measure;

    private static final ArrayList<String> ATTRIBUTE_STRINGS = new ArrayList<>();

    public static final String START_STRING = "article";

    static {
        ATTRIBUTE_STRINGS.add("name");
        ATTRIBUTE_STRINGS.add("quantity");
        ATTRIBUTE_STRINGS.add("measure");
    }



    @Override
    public void setSetters() {
        setters.put(ATTRIBUTE_STRINGS.get(0), this::setName);
        setters.put(ATTRIBUTE_STRINGS.get(1), this::setQuantity);
        setters.put(ATTRIBUTE_STRINGS.get(2), this::setMeasure);
    }

    @Override
    public String getStartString() {
        return START_STRING;
    }

    public String getName() {return name;}
    public double getQuantity() {return quantity;}
    public String getMeasure() {return measure;}

    public void setName(String name) {this.name = name;}
    public void setQuantity(String quantity) {this.quantity = Double.parseDouble(quantity);}
    public void setQuantityDouble(double quantity) {this.quantity = quantity;}
    public void setMeasure(String measure) {this.measure = measure;}
    public void incrementQuantity(double quantity) {
        this.quantity+=quantity;
    }
    public void decrementQuantity(double quantity) {
        this.quantity -= quantity;
        if(this.quantity<0) this.quantity = 0;
    }


    @Override
    public String toString() {

        String toString = "Article{" +
                "name='" + name + '\'' +
                ", quantity='" + quantity + measure + '\'' + "} ";

        return toString;
    }
}
