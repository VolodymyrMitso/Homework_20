package mitso.v.homework_20.databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


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

    public boolean checkIfDatabaseExists(Context context) {

        return (context.getDatabasePath(DATABASE_NAME).exists());
    }
}
