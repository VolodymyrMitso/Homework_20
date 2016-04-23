package mitso.v.homework_20.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import mitso.v.homework_20.api.models.Bank;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "banks_database";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE = "banks_table";

    public static final String KEY_ID  = "_id";
    public static final String COLUMN_BANKS = "banks_column";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DATABASE_TABLE + " " +
                "(" +
                KEY_ID + " integer primary key autoincrement, " +
                COLUMN_BANKS + " text not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addData(final List<Bank> banks) {

        SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();

        db.execSQL("delete from " + DATABASE_TABLE);

        for (int i = 0; i < banks.size(); i++) {

            Bank bank = banks.get(i);

            String bankString = new Gson().toJson(bank);

            ContentValues values = new ContentValues();
            values.put(COLUMN_BANKS, bankString);

            db.insert(DATABASE_TABLE, null, values);
        }

        Log.e("SECOND_THREAD_LOG_TAG", "ADD DATA DONE");

    }

    public List<Bank> getData() {

        final List<Bank> banks = new ArrayList<>();

        SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();

        String[] projection = {
                COLUMN_BANKS
        };

        Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, projection,
                null, null, null, null, null);

        while (cursor.moveToNext()) {

            String bankString = cursor.getString(cursor.getColumnIndex(COLUMN_BANKS));

            Bank bank = new Gson().fromJson(bankString, Bank.class);

            banks.add(bank);
        }

        cursor.close();

        Log.e("THIRD_THREAD_LOG_TAG", "GET DATA DONE");

        return banks;
    }
}
