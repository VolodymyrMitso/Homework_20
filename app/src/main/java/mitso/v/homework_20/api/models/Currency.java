package mitso.v.homework_20.api.models;

public class Currency {

    private String name;
    private String sale;
    private String purchase;

    public void setName(String name) {
        this.name = name;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    @Override
    public String toString() {
        return  "    ----- CURRENCY INFO:\n" +
                "    name = " + name + "\n" +
                "    sale = " + sale + "\n" +
                "    purchase = " + purchase + "\n";
    }
}
