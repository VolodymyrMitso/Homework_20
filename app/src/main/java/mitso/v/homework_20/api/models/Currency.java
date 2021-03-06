package mitso.v.homework_20.api.models;

import java.io.Serializable;

public class Currency implements Serializable {

    private String name;
    private String abbreviation;
    private double purchase;
    private double sale;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }

    public double getPurchase() {
        return purchase;
    }

    public void setPurchase(double purchase) {
        this.purchase = purchase;
    }

    @Override
    public String toString() {
        return  "    ----- CURRENCY INFO:\n" +
                "    name = " + name + "\n" +
                "    abbreviation = " + abbreviation + "\n" +
                "    purchase = " + purchase + "\n" +
                "    sale = " + sale + "\n";
    }
}
