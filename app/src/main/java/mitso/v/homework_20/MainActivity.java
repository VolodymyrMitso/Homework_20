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

import java.util.List;

import mitso.v.homework_20.api.Api;
import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.constansts.Constants;
import mitso.v.homework_20.databse.DatabaseHelper;
import mitso.v.homework_20.databse.GetDataTask;
import mitso.v.homework_20.databse.SetDataTask;
import mitso.v.homework_20.recycler_view.BankAdapter;
import mitso.v.homework_20.recycler_view.IBankHandler;
import mitso.v.homework_20.recycler_view.SpacingDecoration;
import mitso.v.homework_20.service.AlarmReceiver;
import mitso.v.homework_20.service.UpdateService;
import mitso.v.homework_20.support.SupportMain;

public class MainActivity extends AppCompatActivity
        implements IBankHandler, SearchView.OnQueryTextListener {

    private final String        LOG_TAG = Constants.MAIN_ACTIVITY_LOG_TAG;

    public static boolean       isActivityRunning;

    private SupportMain         mSupport;

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

        if (UpdateService.isServiceRunning) {
            mSupport.showToast(this, getResources().getString(R.string.s_updating));
            finish();
        }

        isActivityRunning = true;
        scheduleAlarm();

        setContentView(R.layout.main_activity);
        initActionBar();

        mSupport = new SupportMain();

        if (mSupport.checkIfDatabaseExists(this)) {

            Log.e(LOG_TAG, "DATABASE EXISTS.");
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

            Log.e(LOG_TAG, "DATABASE DOESN'T EXIST.");
            isDatabaseCreated = false;

            if (mSupport.checkConnection(this)) {

                Log.e(LOG_TAG, "CONNECTION - YES.");
                apiGetData();
                isApiRequestSent = true;

            } else {

                Log.e(LOG_TAG, "CONNECTION - NO.");
                mSupport.showToast(this, getResources().getString(R.string.s_fist_run_internet));
                finish();
            }
        }
    }

    public void scheduleAlarm() {

        final Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.TIME_5_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmPendingIntent);
    }

    private void initActionBar() {

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(Html.fromHtml(Constants.FONT_COLOR_1 +
                    Integer.toHexString(getResources().getColor(R.color.c_action_bar_text)).substring(2) +
                    Constants.FONT_COLOR_2 + getResources().getString(R.string.s_app_name) + Constants.FONT_COLOR_3));
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

                mApiBankList = mSupport.getBanksFromData(MainActivity.this, mJsonData);

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
            public void onFailure(Throwable _error) {

                isApiRequestDone = false;

                Log.e(mApiGet.LOG_TAG, "ON FAILURE");
                Log.e(mApiGet.LOG_TAG, mSupport.printException(_error));

                if (mSupport.checkIfDatabaseExists(MainActivity.this))
                    getDatabaseData();
                else {
                    mSupport.showToast(MainActivity.this, getResources().getString(R.string.s_fist_run_server));
                    finish();
                }
            }
        });
    }

    private void getDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void onSuccess(List<Bank> _result) {

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
            public void onFailure(Throwable _error) {

                Log.e(getDataTask.LOG_TAG, "ON FAILURE");
                Log.e(getDataTask.LOG_TAG, mSupport.printException(_error));

                mDatabaseHelper.close();
                getDataTask.releaseCallback();
            }
        });
        getDataTask.execute();
    }

    private void setDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(MainActivity.this);

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void onSuccess() {

                Log.e(setDataTask.LOG_TAG, "ON SUCCESS.");

                mDatabaseHelper.close();
                setDataTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                Log.e(setDataTask.LOG_TAG, "ON FAILURE");
                Log.e(setDataTask.LOG_TAG, mSupport.printException(_error));

                mDatabaseHelper.close();
                setDataTask.releaseCallback();
            }
        });
        setDataTask.execute();
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }, Constants.TIME_2_SECONDS);
                }
            });
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_Banks_AM);
        mBankAdapter = new BankAdapter(this, mBankList);
        mRecyclerView.setAdapter(mBankAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        final int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.d_size_17dp);
        mRecyclerView.addItemDecoration(new SpacingDecoration(spacingInPixels));

        mBankAdapter.setBankHandler(this);
        isRecyclerViewCreated = true;
        isHandlerSet = true;
        Log.e(LOG_TAG, "HANDLER IS SET ON.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.mi_Search_MM);
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
        } catch (Exception _error) {
            mSupport.printException(_error);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UpdateService.isServiceRunning) {
            mSupport.showToast(this, getResources().getString(R.string.s_updating));
            finish();
        }

        isActivityRunning = true;

        try {
            if (!isHandlerSet) {
                mBankAdapter.setBankHandler(this);
                isHandlerSet = true;
                Log.e(LOG_TAG, "HANDLER IS SET ON.");
            }
        } catch (Exception _error) {
            mSupport.printException(_error);
        }
    }

    @Override
    public void goToLink(String _link) {

        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_link));
        if (browserIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(browserIntent, getResources().getString(R.string.choose_program)));
        else
            mSupport.showToast(this, getResources().getString(R.string.necessary_program));
    }

    @Override
    public void showOnMap(String _region, String _city, String _address) {

        final Intent addressIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(Constants.GEO + _region + ", " + _city + ", " + _address));
        if (addressIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(addressIntent, getResources().getString(R.string.choose_program)));
        else
            mSupport.showToast(this, getResources().getString(R.string.necessary_program));
    }

    @Override
    public void callPhone(String _phone) {

        final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(Constants.TEL + _phone));
        if (callIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(callIntent, getResources().getString(R.string.choose_program)));
        else
            mSupport.showToast(this, getResources().getString(R.string.necessary_program));
    }

    @Override
    public void showDetails(Bank _bank) {

        final Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BANK_BUNDLE_KEY, _bank);
        detailsActivityIntent.putExtras(bundle);
        startActivity(detailsActivityIntent);
    }
}