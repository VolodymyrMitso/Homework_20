package mitso.v.homework_20.databse;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_20.api.models.Bank;

public class GetDataTask extends AsyncTask<Void, Void, List<Bank>> {

    public String LOG_TAG = "GET_DATA_TASK_LOG_TAG";

    public interface Callback{
        void success(List<Bank> _result);
        void failure(Throwable _error);
    }

    private DatabaseHelper mDatabaseHelper;
    private List<Bank> mBankList;
    private Callback mCallback;
    private Exception mException;

    public GetDataTask(DatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
        this.mBankList = new ArrayList<>();
    }

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    @Override
    protected List<Bank> doInBackground(Void... params) {

        try {

            SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            String[] projection = {
                    DatabaseHelper.COLUMN_BANKS
            };

            Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, projection,
                    null, null, null, null, null);

            while (cursor.moveToNext()) {

                String bankString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANKS));

                Bank bank = new Gson().fromJson(bankString, Bank.class);

                mBankList.add(bank);
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Bank> banks) {
        super.onPostExecute(banks);

        if (mCallback != null) {
            if (mException == null)
                mCallback.success(mBankList);
            else
                mCallback.failure(mException);
        }
    }
}
