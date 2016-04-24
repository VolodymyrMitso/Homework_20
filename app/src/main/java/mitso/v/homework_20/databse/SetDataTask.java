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

import mitso.v.homework_20.api.models.Bank;

public class SetDataTask extends AsyncTask<Void, Void, Void> {

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

            if (mDatabaseHelper.checkIfDatabaseExists(mContext)) {

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                String[] projection = {
                        DatabaseHelper.COLUMN_BANKS
                };

                Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, projection,
                        null, null, null, null, null);

                if (cursor.moveToNext()) {

                    String bankString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BANKS));

                    Bank bank = new Gson().fromJson(bankString, Bank.class);

                    Log.e("SET_DATA_TASK_LOG_TAG", bank.toString());

                    Date oldDate = bank.getDate();

                    Date newDate = mBankList.get(0).getDate();

                    if (newDate.after(oldDate)) {
                        Log.e("SET_DATA_TASK_LOG_TASK", "NEW DATE IS AFTER OLD DATE");

                        db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

                        for (int i = 0; i < mBankList.size(); i++) {

                            Bank newBank = mBankList.get(i);

                            String newBankString = new Gson().toJson(newBank);

                            ContentValues values = new ContentValues();
                            values.put(DatabaseHelper.COLUMN_BANKS, newBankString);

                            db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                        }
                    } else {
                        Log.e("SET_DATA_TASK_LOG_TAG", "NEW DATE IS NOT AFTER OLD DATE");
                    }
                }

                cursor.close();

            } else {

                Log.e("SET_DATA_TASK_LOG_TAG", "CREATING DB FIRST TIME");

                SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

                db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

                for (int i = 0; i < mBankList.size(); i++) {

                    Bank bank = mBankList.get(i);

                    String bankString = new Gson().toJson(bank);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_BANKS, bankString);

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
