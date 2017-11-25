package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sapergis.parking.ParkingStatistics;

import java.util.ArrayList;
import java.util.List;

import objects.ParkingPositionObject;
import objects.ParkingStatisticsObject;

public class RetrieveFromDatabase {
    private RetrieveFromDatabase(){

    }
    private int counter = 0;
    private static final String [] columnsToRetrieve = {
            ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_AREA,
            ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED,
            ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED_NO,
            ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE
    };

    public static  ParkingPositionObject retrievePosition(SQLiteDatabase readableDatabase , String userName){
        ParkingPositionObject parkingPositionObj;

        String selection = ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME +"=?";
        String [] selectionArgs = {userName};
        Cursor cursor = readableDatabase.query(
                ParkingLocationDBContract.ParkingLocation.TABLE_NAME,
                columnsToRetrieve,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        parkingPositionObj=fetchObject(cursor);
        cursor.close();
        return parkingPositionObj;
    }

    public static ParkingPositionObject retrieveTempPosition(SQLiteDatabase readableDatabase , String userName){
        ParkingPositionObject parkingPositionObj;
        String selection = ParkingLocationDBContract.TempParkingLocation.COLUMN_USERNAME +" = ?";
        String [] selectionArgs = {userName};
        Cursor cursor = readableDatabase.query(
                ParkingLocationDBContract.TempParkingLocation.TABLE_NAME,
                columnsToRetrieve,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        parkingPositionObj = fetchObject(cursor);
        cursor.close();
        return parkingPositionObj;
    }

    public static List<ParkingPositionObject> retrieveAllEntries(SQLiteDatabase readableDatabase , String userName){
        ArrayList<ParkingPositionObject> entries = new ArrayList<>();
        String selection = ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME +" = ?";
        String [] selectionArgs = {userName};
        Cursor cursor = readableDatabase.query(
                ParkingLocationDBContract.ParkingLocation.TABLE_NAME,
                columnsToRetrieve,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        int counter = 0;
        int count =cursor.getCount();
        while(counter < count){
            entries.add(fetchObject(cursor));
            counter++;
            cursor.moveToNext();
        }

        cursor.close();
        return entries;
    }

    public static List<String> retrieveStatistics (SQLiteDatabase readableDatabase , String userName) {
        ArrayList<String> statistics = new ArrayList<>();
        String selection = ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME + " = ?";
        String[] column = {ParkingLocationDBContract.ParkingLocation.COLUMN_AREA};
        String statisticsRawQuery= "SELECT DISTINCT "+column[0]+" FROM "+ParkingLocationDBContract.ParkingLocation.TABLE_NAME+" ";
        String[] selectionArgs = {userName};
       Cursor cursor = readableDatabase.rawQuery( statisticsRawQuery, null);
        /*
       Cursor cursor = readableDatabase.query(
                true,
                ParkingLocationDBContract.ParkingLocation.TABLE_NAME,
                columnsToRetrieve, null, null, columnsToRetrieve[4], null, null, null
        );
        */
        cursor.moveToFirst();
        int counter = 0;
        int count =cursor.getCount();
        while (counter < count) {
            statistics.add(cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA)));
            counter++;
            cursor.moveToNext();
        }
        cursor.close();
        return statistics;
    }

     private static ParkingPositionObject fetchObject(Cursor cursor){
        ParkingPositionObject parkingPositionObj = new ParkingPositionObject();

        parkingPositionObj.setUsername(
                cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME))
        );
        parkingPositionObj.setLongitude(
                cursor.getDouble(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE))
        );
        parkingPositionObj.setLatitude(
                cursor.getDouble(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE))
        );
         parkingPositionObj.setDatetime(
                 cursor.getLong(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME))
         );
        parkingPositionObj.setArea(
                cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA))
        );
         parkingPositionObj.setAddress_parked(
                 cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED))
         );
        parkingPositionObj.setParked_address_no(
                cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED_NO))
        );
         parkingPositionObj.setVehicle(
                 cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE))
         );
        return parkingPositionObj;
    }

}
