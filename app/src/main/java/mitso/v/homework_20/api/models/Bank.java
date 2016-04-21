package mitso.v.homework_20.api.models;

import java.util.List;

public class Bank {

    private String name;
    private String region;
    private String city;
    private String address;
    private String phone;
    private String link;
    private List<Currency> currencies;

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String toString() {
        String result = "----- BANK INFO\n" +
                        "name = " + name + "\n" +
                        "region = " + region + "\n" +
                        "city = " + city + "\n" +
                        "address = " + address + "\n" +
                        "phone = " + phone + "\n" +
                        "link = " + link + "\n" +
                        "currencies = \n";
        for (int i = 0; i < currencies.size(); i++)
            result += currencies.get(i).toString();

        return result;
    }
}
