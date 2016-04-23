package mitso.v.homework_20.databse;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.List;

import mitso.v.homework_20.api.models.Bank;

public class SetDataTask extends AsyncTask<Void, Void, Void> {

    public interface Callback{
        void success();
        void failure(Throwable _error);
    }

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

    public SetDataTask(DatabaseHelper mDatabaseHelper, List<Bank> bankList) {
        this.mDatabaseHelper = mDatabaseHelper;
        this.mBankList = bankList;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

            db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);

            for (int i = 0; i < mBankList.size(); i++) {

                Bank bank = mBankList.get(i);

                String bankString = new Gson().toJson(bank);

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_BANKS, bankString);

                db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
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
