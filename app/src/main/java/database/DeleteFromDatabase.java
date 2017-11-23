package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.sapergis.parking.R;

public class DeleteFromDatabase {

    public static void deleteTempPosition(Context context){
        SQLiteDatabase database = new ParkingDBHelper(context).getWritableDatabase();
        database.delete(ParkingLocationDBContract.TempParkingLocation.TABLE_NAME,
                        null,
                        null  );
        Toast.makeText(context, R.string.position_deleted, Toast.LENGTH_LONG).show();
    }
}
