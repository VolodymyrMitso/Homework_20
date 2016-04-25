package mitso.v.homework_20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_20.api.Api;
import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.databse.DatabaseHelper;
import mitso.v.homework_20.databse.GetDataTask;
import mitso.v.homework_20.databse.SetDataTask;
import mitso.v.homework_20.recycler_view.BankAdapter;
import mitso.v.homework_20.recycler_view.IBankHandler;
import mitso.v.homework_20.recycler_view.SpacingDecoration;
import mitso.v.homework_20.service.MyAlarmReceiver;
import mitso.v.homework_20.support.Support;

public class MainActivity extends AppCompatActivity
        implements IBankHandler, SearchView.OnQueryTextListener {

    private final String LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    private JsonData        mJsonData;
    private List<Bank>      mBankList;

    private RecyclerView        mRecyclerView;
    private SwipeRefreshLayout  mRefreshLayout;
    private BankAdapter         mBankAdapter;
    private boolean             isRecyclerViewCreated;
    private boolean             isHandlerSet;

    private DatabaseHelper  mDatabaseHelper;

    private Support mSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_app_name) + "</font>"));

        scheduleAlarm();

        mSupport = new Support();

        if (mSupport.checkIfDatabaseExists(this)) {

            Log.e(LOG_TAG, "DATABASE EXIST.");

            if (mSupport.checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                getData();
            }

        } else {

            Log.e(LOG_TAG, "DATABASE NOT EXIST.");

            if (mSupport.checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                Toast.makeText(MainActivity.this, "APP NEED WI-FI FOR FIRST RUN.\nSORRY. BYE-BYE.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void scheduleAlarm() {

        final Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, 60000, pIntent);
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

                mBankList = mSupport.getBanksFromData(mJsonData);

                if (mBankList != null) {
                    Log.e(LOG_TAG, String.valueOf(mBankList.size()));
                    Log.e(LOG_TAG, mBankList.get(0).toString());
                    Log.e(LOG_TAG, mBankList.get(mBankList.size() - 1).toString());
                }

                if (!isRecyclerViewCreated)
                    initRecyclerView();
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

    private void initRecyclerView() {

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sl_SwipeLayout_AM);
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.c_action_bar_bg));

            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (mSupport.checkConnection(MainActivity.this)) {
                        apiGetData();
                        mBankAdapter.notifyDataSetChanged();
                    }

                    Toast.makeText(MainActivity.this, "REFRESH", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }, 2000);
                }
            });
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_Banks_AM);
        mBankAdapter = new BankAdapter(mBankList);
        mRecyclerView.setAdapter(mBankAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_17dp);
        mRecyclerView.addItemDecoration(new SpacingDecoration(spacingInPixels));

        mBankAdapter.setBankHandler(this);
        isRecyclerViewCreated = true;
        isHandlerSet = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem item = menu.findItem(R.id.mi_Search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String _query) {

        final List<Bank> filteredBankList = filter(mBankList, _query);
        mBankAdapter.animateTo(filteredBankList);
        mRecyclerView.scrollToPosition(0);

        return true;
    }

    private List<Bank> filter(List<Bank> _bankList, String _query) {
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


    @Override
    public void onPause() {
        super.onPause();

        if (isHandlerSet)
            mBankAdapter.releaseBankHandler();
    }

    private void setData() {

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void success() {
                Log.e(setDataTask.LOG_TAG, "SET DATA DONE.");

                mDatabaseHelper.close();
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

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void success(List<Bank> _result) {
                mBankList = _result;

                initRecyclerView();

                Log.e(getDataTask.LOG_TAG, "GET DATA DONE.");

                Log.e(getDataTask.LOG_TAG, mBankList.get(0).toString());
                Log.e(getDataTask.LOG_TAG, mBankList.get(mBankList.size() - 1).toString());

                mDatabaseHelper.close();
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