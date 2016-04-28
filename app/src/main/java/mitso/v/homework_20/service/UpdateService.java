package mitso.v.homework_20.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import mitso.v.homework_20.DetailsActivity;
import mitso.v.homework_20.MainActivity;
import mitso.v.homework_20.api.Api;
import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.databse.DatabaseHelper;
import mitso.v.homework_20.databse.GetDataTask;
import mitso.v.homework_20.databse.SetDataTask;
import mitso.v.homework_20.support.SupportMain;

public class UpdateService extends IntentService {

    public static boolean   isServiceRunning;

    private SupportMain     mSupport;

    private JsonData        mJsonData;
    private List<Bank>      mBankList;
    private List<Bank>      mApiBankList;
    private List<Bank>      mDatabaseBankList;

    private Api mApiGet;

    private DatabaseHelper  mDatabaseHelper;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent _intent) {

        isServiceRunning = true;

        mSupport = new SupportMain();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!MainActivity.isActivityRunning && !DetailsActivity.isActivityRunning) {

                    if (mSupport.checkConnection(getApplicationContext()))

                        apiGetData();
                } else
                    Toast.makeText(UpdateService.this, "ACTIVITIES ARE RUNNING", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void apiGetData() {

        mApiGet = new Api();
        mApiGet.getData(new IConnectCallback() {
            @Override
            public void onSuccess(Object object) {

                Log.e(mApiGet.LOG_TAG, "ON SUCCESS");

                mJsonData = (JsonData) object;

//                Log.e(LOG_TAG, mJsonData.print_1());
//                Log.e(LOG_TAG, mJsonData.print_2());
//                Log.e(LOG_TAG, mJsonData.print_3());

                mApiBankList =  mSupport.getBanksFromData(mJsonData);

                if (mApiBankList != null) {
                    Log.e(mApiGet.LOG_TAG, String.valueOf(mApiBankList.size()));
                    Log.e(mApiGet.LOG_TAG, mApiBankList.get(0).toString());
                    Log.e(mApiGet.LOG_TAG, mApiBankList.get(mApiBankList.size() - 1).toString());
                }

                getDatabaseData();
            }

            @Override
            public void onFailure(Throwable _error) {

                isServiceRunning = false;

                Log.e(mApiGet.LOG_TAG, "ON FAILURE");
                Log.e(mApiGet.LOG_TAG, mSupport.printException(_error));
            }
        });
    }

    private void getDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void onSuccess(List<Bank> _result) {

                mDatabaseBankList = _result;

                Log.e(getDataTask.LOG_TAG, "ON SUCCESS.");

                if (mDatabaseBankList != null) {
                    Log.e(getDataTask.LOG_TAG, String.valueOf(mDatabaseBankList.size()));
                    Log.e(getDataTask.LOG_TAG, mDatabaseBankList.get(0).toString());
                    Log.e(getDataTask.LOG_TAG, mDatabaseBankList.get(mDatabaseBankList.size() - 1).toString());
                }

                mBankList = mSupport.getUnitedBanks(mApiBankList, mDatabaseBankList);
                setDatabaseData();

                mDatabaseHelper.close();
                getDataTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                isServiceRunning = false;

                Log.e(getDataTask.LOG_TAG, "ON FAILURE");
                Log.e(getDataTask.LOG_TAG, mSupport.printException(_error));

                getDataTask.releaseCallback();
            }
        });
        getDataTask.execute();
    }

    private void setDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        final SetDataTask setDataTask = new SetDataTask(getApplicationContext(), mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void onSuccess() {

                isServiceRunning = false;

                Log.e(setDataTask.LOG_TAG, "SET DATA DONE.");

                mDatabaseHelper.close();
                setDataTask.releaseCallback();
            }

            @Override
            public void onFailure(Throwable _error) {

                isServiceRunning = false;

                Log.e(setDataTask.LOG_TAG, "ON FAILURE");
                Log.e(setDataTask.LOG_TAG, mSupport.printException(_error));

                setDataTask.releaseCallback();
            }
        });
        setDataTask.execute();
    }
}
