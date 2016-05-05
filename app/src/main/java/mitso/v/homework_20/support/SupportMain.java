package mitso.v.homework_20.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mitso.v.homework_20.R;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.Currency;
import mitso.v.homework_20.api.models.json.JsonCurrency;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.api.models.json.JsonOrganization;
import mitso.v.homework_20.databse.DatabaseHelper;

public class SupportMain {

    public boolean checkIfDatabaseExists(Context _context) {

        return (_context.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists());
    }

    public boolean checkConnection(Context _context) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifiInfo != null && wifiInfo.isConnected()) || (networkInfo != null && networkInfo.isConnected()));
    }

    public List<Bank> getBanksFromData(JsonData jsonData) {

        List<Bank> banks = new ArrayList<>();

        if (jsonData != null) {

            List<JsonOrganization> jsonOrganizations = jsonData.getOrganizations();
            Map<String, String> currenciesNamesAbbreviations = jsonData.getCurrencies();
            Map<String, String> regionsNamesIds = jsonData.getRegions();
            Map<String, String> citiesNamesIds = jsonData.getCities();

            for (int i = 0; i < jsonOrganizations.size(); i++) {

                JsonOrganization jsonOrganization = jsonOrganizations.get(i);
                Bank bank = new Bank();
                bank.setId(jsonOrganization.getId());
                bank.setName(jsonOrganization.getTitle());
                bank.setRegion(regionsNamesIds.get(jsonOrganization.getRegionId()));
                bank.setCity(citiesNamesIds.get(jsonOrganization.getCityId()));
                bank.setAddress(jsonOrganization.getAddress());
                bank.setPhone(jsonOrganization.getPhone());
                bank.setLink(jsonOrganization.getLink());

                ArrayList<JsonCurrency> jsonCurrencies = jsonOrganization.getCurrencies();
                ArrayList<Currency>  currencies = new ArrayList<>();
                for (int j = 0; j < jsonCurrencies.size(); j++) {
                    JsonCurrency jsonCurrency = jsonCurrencies.get(j);
                    Currency currency = new Currency();
                    currency.setName(currenciesNamesAbbreviations.get(jsonCurrency.getName()));
                    currency.setAbbreviation(jsonCurrency.getName());
                    currency.setSale(Double.parseDouble(jsonCurrency.getAsk()));
                    currency.setPurchase(Double.parseDouble(jsonCurrency.getBid()));
                    currencies.add(currency);
                }
                bank.setCurrencies(currencies);

                bank.setDate(jsonData.getDate());

                banks.add(bank);
            }
        }

        return banks;
    }

    public String printException(Throwable _error) {

        StringWriter errors = new StringWriter();
        _error.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public List<Bank> filter(List<Bank> _bankList, String _query) {
        _query = _query.toLowerCase();

        final List<Bank> filteredModelList = new ArrayList<>();
        for (Bank bank : _bankList) {
            final String name = bank.getName().toLowerCase();
            final String city = bank.getCity().toLowerCase();
            final String region = bank.getRegion().toLowerCase();
            if (name.contains(_query)
                    || city.contains(_query)
                    || region.contains(_query))
                filteredModelList.add(bank);
        }
        return filteredModelList;
    }

    public List<Bank> getUnitedBanks(List<Bank> apiBanks, List<Bank> databaseBanks) {

        List<Bank> unitedBanks = new ArrayList<>();

        unitedBanks.addAll(databaseBanks);

        for (Bank apiBank : apiBanks)
            if (!unitedBanks.contains(apiBank))
                unitedBanks.add(apiBank);

        for (int i = 0; i < unitedBanks.size(); i++) {
            for (int j = 0; j < unitedBanks.size(); ) {
                Bank bankI = unitedBanks.get(i);
                Bank bankJ = unitedBanks.get(j);
                if (i != j) {
                    if (bankI.getId().equals(bankJ.getId()) && bankI.getDate().after(bankJ.getDate())) {

                        unitedBanks.remove(j);
                        j = 0;
                        i = 0;

                    } else
                        j++;
                } else
                    j++;
            }
        }

        Collections.sort(unitedBanks, new Comparator<Bank>() {
            @Override
            public int compare(Bank bank1, Bank bank2) {
                return bank1.getName().toLowerCase().compareTo(bank2.getName().toLowerCase());
            }
        });

        return unitedBanks;
    }

    public void showToast(Context _context, String _string) {

        final TextView textView = new TextView(_context);
        textView.setBackgroundColor(_context.getResources().getColor(R.color.c_toast_bg));
        textView.setTextColor(_context.getResources().getColor(R.color.c_toast_text));
        textView.setText(_string);
        textView.setGravity(Gravity.CENTER);
        final int padding = _context.getResources().getDimensionPixelSize(R.dimen.d_size_15dp);
        textView.setPadding(padding, padding, padding, padding);

        final Toast toast = new Toast(_context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(textView);
        toast.show();
    }
}
