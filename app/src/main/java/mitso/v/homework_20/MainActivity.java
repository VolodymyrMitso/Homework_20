package mitso.v.homework_20;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mitso.v.homework_20.api.Api;
import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.Currency;
import mitso.v.homework_20.api.models.json.JsonCurrency;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.api.models.json.JsonOrganization;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    private JsonData mJsonData;
    private List<Bank> mBankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Api.getData(new IConnectCallback() {
            @Override
            public void onSuccess(Object object) {

                mJsonData = (JsonData) object;

                Log.e(LOG_TAG, mJsonData.print_1());
                Log.e(LOG_TAG, mJsonData.print_2());
                Log.e(LOG_TAG, mJsonData.print_3());

                Log.e(LOG_TAG, "onSuccess");

                mBankList = getBanksFromData(mJsonData);

                if (mBankList != null) {
                    Log.e(LOG_TAG, String.valueOf(mBankList.size()));
                    Log.e(LOG_TAG, mBankList.get(0).toString());
                    Log.e(LOG_TAG, mBankList.get(mBankList.size() - 1).toString());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

                StringWriter errors = new StringWriter();
                throwable.printStackTrace(new PrintWriter(errors));
                Log.e(LOG_TAG, "onFailure");
                Log.e(LOG_TAG, errors.toString());
            }
        });
    }

    private ArrayList<Bank> getBanksFromData(JsonData jsonData) {

        ArrayList<Bank> banks = new ArrayList<>();

        if (jsonData != null) {

            List<JsonOrganization> jsonOrganizations = jsonData.getOrganizations();
            Map<String, String> currenciesNamesAbbreviations = jsonData.getCurrencies();
            Map<String, String> regionsNamesIds = jsonData.getRegions();
            Map<String, String> citiesNamesIds = jsonData.getCities();

            for (int i = 0; i < jsonOrganizations.size(); i++) {

                JsonOrganization jsonOrganization = jsonOrganizations.get(i);
                Bank bank = new Bank();
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
                    currency.setSale(jsonCurrency.getAsk());
                    currency.setPurchase(jsonCurrency.getBid());
                    currencies.add(currency);
                }
                bank.setCurrencies(currencies);

                banks.add(bank);
            }
        }

        return banks;
    }
}
