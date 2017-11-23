package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ParkingDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "ParkingDatabase";
    private static final String DROP_TABLE_IF_EXISTS= "DROP TABLE IF EXISTS ";

    public ParkingDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ParkingLocationDBContract.ParkingLocation.CREATE_TABLE);
        sqLiteDatabase.execSQL(ParkingLocationDBContract.TempParkingLocation.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_IF_EXISTS + ParkingLocationDBContract.ParkingLocation.TABLE_NAME);
        sqLiteDatabase.execSQL(DROP_TABLE_IF_EXISTS + ParkingLocationDBContract.TempParkingLocation.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
