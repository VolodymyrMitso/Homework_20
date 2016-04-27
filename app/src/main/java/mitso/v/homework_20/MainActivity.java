package mitso.v.homework_20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import mitso.v.homework_20.service.AlarmReceiver;
import mitso.v.homework_20.support.Support;

public class MainActivity extends AppCompatActivity
        implements IBankHandler, SearchView.OnQueryTextListener {

    private final String        LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    public static boolean       isActivityRunning;

    private Support             mSupport;

    private JsonData            mJsonData;
    private List<Bank>          mBankList;
    private List<Bank>          mApiBankList;
    private List<Bank>          mDatabaseBankList;

    private Api                 mApiGet;
    private boolean             isApiRequestSent;
    private boolean             isApiRequestDone;

    private DatabaseHelper      mDatabaseHelper;
    private boolean             isDatabaseCreated;

    private RecyclerView        mRecyclerView;
    private SwipeRefreshLayout  mRefreshLayout;
    private BankAdapter         mBankAdapter;
    private boolean             isRecyclerViewCreated;
    private boolean             isHandlerSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isActivityRunning = true;
        scheduleAlarm();

        setContentView(R.layout.main_activity);
        initActionBar();

        mSupport = new Support();

        if (mSupport.checkIfDatabaseExists(this)) {

            Log.e(LOG_TAG, "DATABASE EXIST.");
            isDatabaseCreated = true;

            if (mSupport.checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();
                isApiRequestSent = true;

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                getDatabaseData();
                isApiRequestSent = false;
            }

        } else {

            Log.e(LOG_TAG, "DATABASE NOT EXIST.");
            isDatabaseCreated = false;

            if (mSupport.checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();
                isApiRequestSent = true;

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                Toast.makeText(MainActivity.this, "APP NEED WI-FI FOR FIRST RUN.\nTURN WI-FI ON\nSORRY. BYE-BYE.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initActionBar() {

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#" +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    "'>" + getResources().getString(R.string.s_app_name) + "</font>"));
    }

    public void scheduleAlarm() {

        final Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, AlarmManager.INTERVAL_HALF_HOUR, alarmPendingIntent);
    }


    private void apiGetData() {

        mApiGet = new Api();
        mApiGet.getData(new IConnectCallback() {
            @Override
            public void onSuccess(Object object) {

                Log.e(mApiGet.LOG_TAG, "ON SUCCESS");
                isApiRequestDone = true;

                mJsonData = (JsonData) object;

//                Log.e(LOG_TAG, mJsonData.print_1());
//                Log.e(LOG_TAG, mJsonData.print_2());
//                Log.e(LOG_TAG, mJsonData.print_3());

                mApiBankList = mSupport.getBanksFromData(mJsonData);

                if (mApiBankList != null) {
                    Log.e(mApiGet.LOG_TAG, "BANKS COUNT : " + String.valueOf(mApiBankList.size()));
                    Log.e(mApiGet.LOG_TAG, mApiBankList.get(0).toString());
                    Log.e(mApiGet.LOG_TAG, mApiBankList.get(mApiBankList.size() - 1).toString());
                }

                if (isDatabaseCreated) {
                    getDatabaseData();
                } else {
                    mBankList = mApiBankList;
                    if (!isRecyclerViewCreated) {
                        initRecyclerView();
                        Log.e(LOG_TAG, "RECYCLER VIEW CREATED.");
                        Log.e(LOG_TAG, "LIST = API.");
                    }
                    setDatabaseData();
                }
            }

            @Override
            public void onFailure(Throwable _throwable) {

                isApiRequestDone = false;

                if (mSupport.checkIfDatabaseExists(MainActivity.this))
                    getDatabaseData();
                else {
                    Toast.makeText(MainActivity.this, "SERVER IS DEAD.\nTRY AGAIN LATER.\nSORRY. BYE-BYE.", Toast.LENGTH_LONG).show();
                    finish();
                }

                Log.e(mApiGet.LOG_TAG, "ON FAILURE");
                Log.e(mApiGet.LOG_TAG, mSupport.printException(_throwable));
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
                        isApiRequestSent = true;
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
        mBankAdapter = new BankAdapter(this, mBankList);
        mRecyclerView.setAdapter(mBankAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_17dp);
        mRecyclerView.addItemDecoration(new SpacingDecoration(spacingInPixels));

        mBankAdapter.setBankHandler(this);
        isRecyclerViewCreated = true;
        isHandlerSet = true;
        Log.e(LOG_TAG, "HANDLER IS SET ON.");
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

        if (isRecyclerViewCreated) {

            final List<Bank> filteredBankList = mSupport.filter(mBankList, _query);
            mBankAdapter.animateTo(filteredBankList);
            mRecyclerView.scrollToPosition(0);
        }

        return true;
    }


    @Override
    public void onPause() {
        super.onPause();

        isActivityRunning = false;

        try {
            if (isHandlerSet) {
                mBankAdapter.releaseBankHandler();
                isHandlerSet = false;
                Log.e(LOG_TAG, "HANDLER IS SET OFF.");
            }
        } catch (Exception e) {
            mSupport.printException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        isActivityRunning = true;

        try {
            if (!isHandlerSet) {
                mBankAdapter.setBankHandler(this);
                isHandlerSet = true;
                Log.e(LOG_TAG, "HANDLER IS SET ON.");
            }
        } catch (Exception e) {
            mSupport.printException(e);
        }
    }

    private void setDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void success() {
                Log.e(setDataTask.LOG_TAG, "ON SUCCESS.");

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

    private void getDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void success(List<Bank> _result) {
                mDatabaseBankList = _result;

                Log.e(getDataTask.LOG_TAG, "ON SUCCESS.");

                if (mDatabaseBankList != null) {
                    Log.e(getDataTask.LOG_TAG, "BANKS COUNT : " + String.valueOf(mDatabaseBankList.size()));
                    Log.e(getDataTask.LOG_TAG, mDatabaseBankList.get(0).toString());
                    Log.e(getDataTask.LOG_TAG, mDatabaseBankList.get(mDatabaseBankList.size() - 1).toString());
                }

                if (isApiRequestSent) {

                    if (isApiRequestDone) {

                        mBankList = mSupport.getUnitedBanks(mApiBankList, mDatabaseBankList);
                        if (!isRecyclerViewCreated) {
                            initRecyclerView();
                            Log.e(LOG_TAG, "RECYCLER VIEW CREATED.");
                            Log.e(LOG_TAG, "LIST = API + DATABASE.");
                        } else {
                            mBankAdapter.notifyDataSetChanged();
                            Log.e(LOG_TAG, "RECYCLER VIEW UPDATED.");
                            Log.e(LOG_TAG, "LIST = API + DATABASE.");
                        }

                        setDatabaseData();

                    } else {

                        mBankList = mDatabaseBankList;
                        if (!isRecyclerViewCreated) {
                            initRecyclerView();
                            Log.e(LOG_TAG, "RECYCLER VIEW CREATED.");
                            Log.e(LOG_TAG, "LIST = DATABASE.");
                        } else {
                            mBankAdapter.notifyDataSetChanged();
                            Log.e(LOG_TAG, "RECYCLER VIEW UPDATED.");
                            Log.e(LOG_TAG, "LIST = DATABASE.");
                        }
                    }

                } else {

                    mBankList = mDatabaseBankList;
                    if (!isRecyclerViewCreated) {
                        initRecyclerView();
                        Log.e(LOG_TAG, "RECYCLER VIEW CREATED.");
                        Log.e(LOG_TAG, "LIST = DATABASE.");
                    }
                }

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
    public void goToLink(String _link) {
        Toast.makeText(MainActivity.this, _link, Toast.LENGTH_SHORT).show();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_link));

        if (browserIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(browserIntent, "Choose browser:"));
    }

    @Override
    public void showOnMap(String _region, String _city, String _address) {

        Intent addressIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q=" + _region + ", " + _city + ", " + _address));

        if (addressIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(addressIntent, "Choose program to show on map:"));
        else
            Toast.makeText(MainActivity.this, "You have no maps program", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void callPhone(String _phone) {
        Toast.makeText(MainActivity.this, _phone, Toast.LENGTH_SHORT).show();

        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+38" + _phone));

        if (callIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(callIntent, "Choose how to call:"));
    }

    @Override
    public void showDetails(Bank _bank) {

        Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bank", _bank);
        detailsActivityIntent.putExtras(bundle);
        startActivity(detailsActivityIntent);
    }
}