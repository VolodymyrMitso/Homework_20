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

import mitso.v.homework_20.api.Api;
import mitso.v.homework_20.api.interfaces.IConnectCallback;
import mitso.v.homework_20.api.models.Bank;
import mitso.v.homework_20.api.models.json.JsonData;
import mitso.v.homework_20.databse.DatabaseHelper;
import mitso.v.homework_20.databse.SetDataTask;
import mitso.v.homework_20.support.Support;

public class MyTestService extends IntentService {

    private final String LOG_TAG = "MY_TEST_SERVICE_LOG_TAG";

    private JsonData mJsonData;
    private List<Bank> mBankList;
    private DatabaseHelper mDatabaseHelper;

    private Support mSupport;

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mSupport = new Support();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mSupport.checkConnection(getApplicationContext()))
                    apiGetData();
            }
        });
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

                mBankList =  mSupport.getBanksFromData(mJsonData);

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

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        final SetDataTask setDataTask = new SetDataTask(this, mDatabaseHelper, mBankList);
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
