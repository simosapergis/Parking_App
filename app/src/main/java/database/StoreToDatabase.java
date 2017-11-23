package database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.sapergis.parking.R;

import objects.ParkingPositionObject;

public class StoreToDatabase {
  private StoreToDatabase(){

  }

  public static void storePosition(ParkingPositionObject parkingPositionObj, SQLiteDatabase database){
    ParkingPositionObject ppo = null;
    ppo = parkingPositionObj;
    ContentValues values = new ContentValues();
    values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME, ppo.getUsername());
    values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE , ppo.getLatitude());
    values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE , ppo.getLongitude());
    values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME , ppo.getDatetime());
    values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA , ppo.getArea());
    database.insert(ParkingLocationDBContract.ParkingLocation.TABLE_NAME , null, values);
  }

  public static void storeTempPosition(ParkingPositionObject parkingPositionObj, SQLiteDatabase database){
    ParkingPositionObject ppo = null;
    ppo = parkingPositionObj;
    ContentValues values = new ContentValues();
    values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_USERNAME, ppo.getUsername());
    values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_LATITUDE , ppo.getLatitude());
    values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_LONGITUDE , ppo.getLongitude());
    values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_DATETIME , ppo.getDatetime());
    values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_AREA , ppo.getArea());
    database.insert(ParkingLocationDBContract.TempParkingLocation.TABLE_NAME , null, values);
  }

}
