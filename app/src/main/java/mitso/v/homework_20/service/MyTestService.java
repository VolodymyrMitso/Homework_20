package mitso.v.homework_20.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import mitso.v.homework_20.databse.DatabaseHelper;
import mitso.v.homework_20.databse.SetDataTask;

public class MyTestService extends IntentService {

    private final String LOG_TAG = "MY_TEST_SERVICE_LOG_TAG";

    private JsonData mJsonData;
    private List<Bank> mBankList;
    private DatabaseHelper mDatabaseHelper;

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (checkConnection(getApplicationContext()))
                    apiGetData();
            }
        });
    }

    private boolean checkConnection(Context context) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifiInfo != null && wifiInfo.isConnected()) || (networkInfo != null && networkInfo.isConnected()));
    }

    private void apiGetData() {

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        Api.getData(new IConnectCallback() {
            @Override
            public void onSuccess(Object object) {

                Log.e(LOG_TAG, "onSuccess");

                mJsonData = (JsonData) object;

                Log.e(LOG_TAG, mJsonData.print_1());
                Log.e(LOG_TAG, mJsonData.print_2());
                Log.e(LOG_TAG, mJsonData.print_3());

                mBankList = getBanksFromData(mJsonData);

                if (mBankList != null) {
                    Log.e(LOG_TAG, String.valueOf(mBankList.size()));
                    Log.e(LOG_TAG, mBankList.get(0).toString());
                    Log.e(LOG_TAG, mBankList.get(mBankList.size() - 1).toString());

                    Toast.makeText(getApplication(), String.valueOf(mBankList.size()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplication(), mBankList.get(0).toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplication(), mBankList.get(mBankList.size() - 1).toString(), Toast.LENGTH_SHORT).show();
                }

                setData();
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

    private void setData() {

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void success() {
                Log.e(setDataTask.LOG_TAG, "SET DATA DONE.");

                Toast.makeText(getApplication(), "SET DATA DONE.", Toast.LENGTH_SHORT).show();

                setDataTask.releaseCallback();
            }

            @Override
            public void failure(Throwable _error) {
                // TODO: handle this ...
            }
        });
        setDataTask.execute();
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

                bank.setDate(jsonData.getDate());

                banks.add(bank);
            }
        }

        return banks;
    }
}
