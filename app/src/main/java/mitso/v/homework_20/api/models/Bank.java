package mitso.v.homework_20.api.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Bank implements Serializable {

    private String name;
    private String region;
    private String city;
    private String address;
    private String phone;
    private String link;
    private List<Currency> currencies;
    private Date date;

    @Override
    public String toString() {
        String result = "----- BANK INFO\n" +
                        "name = " + name + "\n" +
                        "region = " + region + "\n" +
                        "city = " + city + "\n" +
                        "address = " + address + "\n" +
                        "phone = " + phone + "\n" +
                        "link = " + link + "\n" +
                        "date = " + new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss").format(date) + "\n" +
                        "currencies = \n";
        for (int i = 0; i < currencies.size(); i++)
            result += currencies.get(i).toString();

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bank)) return false;

        Bank bank = (Bank) o;

        return  getName().equals(bank.getName())
                && getRegion().equals(bank.getRegion())
                && getCity().equals(bank.getCity())
                && getAddress().equals(bank.getAddress())
                && getDate().equals(bank.getDate());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getRegion().hashCode();
        result = 31 * result + getCity().hashCode();
        result = 31 * result + getAddress().hashCode();
        result = 31 * result + getDate().hashCode();
        return result;
    }

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

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getLink() {
        return link;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
