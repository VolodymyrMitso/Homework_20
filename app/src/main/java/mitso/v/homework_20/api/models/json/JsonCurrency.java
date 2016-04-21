package mitso.v.homework_20.api.models.json;

import java.io.Serializable;

public class JsonCurrency implements Serializable {

    private String name;
    private String ask;
    private String bid;

    public void setName(String name) {
        this.name = name;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getName() {
        return name;
    }

    public String getAsk() {
        return ask;
    }

    public String getBid() {
        return bid;
    }

    @Override
    public String toString() {
        return  "       CURRENCY INFO:\n" +
                "       name = " + name + "\n" +
                "       ask = " + ask + "\n" +
                "       bid = " + bid + "\n";
    }
}
