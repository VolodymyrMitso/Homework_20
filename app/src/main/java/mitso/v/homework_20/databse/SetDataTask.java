package mitso.v.homework_20.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import mitso.v.homework_20.Support;
import mitso.v.homework_20.api.models.Bank;

public class SetDataTask extends AsyncTask<Void, Void, Void> {

    public String LOG_TAG = "SET_DATA_TASK_LOG_TAG";

    public interface Callback{
        void success();
        void failure(Throwable _error);
    }

    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private List<Bank> mBankList;
    private Callback mCallback;
    private Exception mException;

    public void setCallback(Callback _callback) {
        mCallback = _callback;
    }

    public void releaseCallback() {
        mCallback = null;
    }

    public SetDataTask(Context _context, DatabaseHelper _databaseHelper, List<Bank> _bankList) {
        this.mContext = _context;
        this.mDatabaseHelper = _databaseHelper;
        this.mBankList = _bankList;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            if (new Support().checkIfDatabaseExists(mContext)) {

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
                Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE,
                        new String[] {DatabaseHelper.COLUMN_BANKS},
                        null, null, null, null, null);

                if (cursor.moveToNext()) {
                    String databaseBankString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANKS));
                    Bank databaseBank = new Gson().fromJson(databaseBankString, Bank.class);

                    Log.e(LOG_TAG, databaseBank.toString());

                    Date databaseDate = databaseBank.getDate();
                    Date apiDate = mBankList.get(0).getDate();

                    if (apiDate.after(databaseDate)) {
                        Log.e(LOG_TAG, "API DATE IS AFTER DATABASE DATE.");

                        db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

                        for (int i = 0; i < mBankList.size(); i++) {

                            Bank apiBank = mBankList.get(i);
                            String apiBankString = new Gson().toJson(apiBank);

                            ContentValues values = new ContentValues();
                            values.put(DatabaseHelper.COLUMN_BANKS, apiBankString);

                            db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                        }
                    } else {
                        Log.e(LOG_TAG, "API DATE IS NOT AFTER DATABASE DATE.");
                    }
                }

                cursor.close();

            } else {

                Log.e(LOG_TAG, "CREATING DATABASE FIRST TIME.");

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

//                db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

                for (int i = 0; i < mBankList.size(); i++) {

                    Bank apiBank = mBankList.get(i);
                    String apiBankString = new Gson().toJson(apiBank);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_BANKS, apiBankString);

                    db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mCallback != null) {
            if (mException == null)
                mCallback.success();
            else
                mCallback.failure(mException);
        }
    }
}
