package mitso.v.homework_20.recycler_view;

import mitso.v.homework_20.api.models.Bank;

public interface IBankHandler {

    void goToLink(String _link);
    void showOnMap(String _region, String _city, String _address);
    void callPhone(String _phone);
    void showDetails(Bank _bank);
}
