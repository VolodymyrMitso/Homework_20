package mitso.v.homework_20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import mitso.v.homework_20.databse.GetDataTask;
import mitso.v.homework_20.databse.SetDataTask;
import mitso.v.homework_20.recycler_view.BankAdapter;
import mitso.v.homework_20.recycler_view.IBankHandler;
import mitso.v.homework_20.recycler_view.SpacingDecoration;
import mitso.v.homework_20.service.MyAlarmReceiver;

public class MainActivity extends AppCompatActivity implements IBankHandler {

    private final String LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    private JsonData        mJsonData;
    private List<Bank>      mBankList;

    private RecyclerView    mRecyclerView_Banks;
    private BankAdapter     mBankAdapter;
    private boolean         isHandlerSet;

    private DatabaseHelper  mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_app_name) + "</font>"));

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        if (mDatabaseHelper.checkIfDatabaseExists(this)) {

            Log.e(LOG_TAG, "DATABASE EXIST.");

            if (checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                getData();
            }

        } else {

            Log.e(LOG_TAG, "DATABASE NOT EXIST.");

            if (checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                Toast.makeText(MainActivity.this, "APP NEED WI-FI FOR FIRST RUN.\nSORRY. BYE-BYE.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        scheduleAlarm();
    }

    private boolean checkConnection(Context context) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((wifiInfo != null && wifiInfo.isConnected()) || (networkInfo != null && networkInfo.isConnected()));
    }

    public void scheduleAlarm() {

        final Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }

    private void apiGetData() {

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
                }

                try {
                    initRecyclerView();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                initRecyclerView();
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

    private void initRecyclerView() {

        mRecyclerView_Banks = (RecyclerView) findViewById(R.id.rv_Banks_AM);
        mBankAdapter = new BankAdapter(mBankList);
        mRecyclerView_Banks.setAdapter(mBankAdapter);
        mRecyclerView_Banks.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_17dp);
        mRecyclerView_Banks.addItemDecoration(new SpacingDecoration(spacingInPixels));

        mBankAdapter.setBankHandler(MainActivity.this);
        isHandlerSet = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isHandlerSet)
            mBankAdapter.releaseBankHandler();
    }

    private void setData() {

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void success() {
                Log.e(setDataTask.LOG_TAG, "SET DATA DONE.");

                setDataTask.releaseCallback();
            }

            @Override
            public void failure(Throwable _error) {
                // TODO: handle this ...
            }
        });
        setDataTask.execute();
    }

    private void getData() {

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void success(List<Bank> _result) {
                mBankList = _result;

                initRecyclerView();

                Log.e(getDataTask.LOG_TAG, "GET DATA DONE.");

                Log.e(getDataTask.LOG_TAG, mBankList.get(0).toString());
                Log.e(getDataTask.LOG_TAG, mBankList.get(mBankList.size() - 1).toString());

                getDataTask.releaseCallback();
            }

            @Override
            public void failure(Throwable _error) {
                // TODO: handle this ...
            }
        });
        getDataTask.execute();
    }

    @Override
    public void goToLink() {
        Toast.makeText(MainActivity.this, "GO TO LINK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showOnMap() {
        Toast.makeText(MainActivity.this, "SHOW ON MAP", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void callPhone() {
        Toast.makeText(MainActivity.this, "PHONE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDetails() {
        Toast.makeText(MainActivity.this, "DETAILS", Toast.LENGTH_SHORT).show();
    }
}