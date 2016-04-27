package mitso.v.homework_20.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import mitso.v.homework_20.support.Support;

public class UpdateService extends IntentService {

    private final String LOG_TAG = "MY_TEST_SERVICE_LOG_TAG";

    private JsonData mJsonData;
    private List<Bank> mBankList;
    private DatabaseHelper mDatabaseHelper;

    private Support mSupport;

    private Api mApiGet;

    private List<Bank>      mApiBankList;
    private List<Bank>      mDatabaseBankList;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mSupport = new Support();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!MainActivity.isActivityRunning && !DetailsActivity.isActivityRunning) {
                    Toast.makeText(UpdateService.this, "ACTIVITIES ARE NOT RUNNING", Toast.LENGTH_SHORT).show();
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

                Log.e(LOG_TAG, "onSuccess");

                mJsonData = (JsonData) object;

                Log.e(LOG_TAG, mJsonData.print_1());
                Log.e(LOG_TAG, mJsonData.print_2());
                Log.e(LOG_TAG, mJsonData.print_3());

                mApiBankList =  mSupport.getBanksFromData(mJsonData);

                if (mApiBankList != null) {
                    Log.e(LOG_TAG, String.valueOf(mApiBankList.size()));
                    Log.e(LOG_TAG, mApiBankList.get(0).toString());
                    Log.e(LOG_TAG, mApiBankList.get(mApiBankList.size() - 1).toString());

                    Toast.makeText(getApplication(), String.valueOf(mApiBankList.size()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplication(), mApiBankList.get(0).toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplication(), mApiBankList.get(mApiBankList.size() - 1).toString(), Toast.LENGTH_SHORT).show();
                }

                getDatabaseData();
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

    private void getDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        final GetDataTask getDataTask = new GetDataTask(mDatabaseHelper);
        getDataTask.setCallback(new GetDataTask.Callback() {
            @Override
            public void success(List<Bank> _result) {
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
            public void failure(Throwable _error) {

                StringWriter errors = new StringWriter();
                _error.printStackTrace(new PrintWriter(errors));
                Log.e(LOG_TAG, "onFailure");
                Log.e(LOG_TAG, errors.toString());
            }
        });
        getDataTask.execute();
    }

    private void setDatabaseData() {

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        final SetDataTask setDataTask = new SetDataTask(getApplicationContext(), mDatabaseHelper, mBankList);
        setDataTask.setCallback(new SetDataTask.Callback() {
            @Override
            public void success() {
                Log.e(setDataTask.LOG_TAG, "SET DATA DONE.");

                Toast.makeText(getApplication(), "SET DATA DONE.", Toast.LENGTH_SHORT).show();

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
}
