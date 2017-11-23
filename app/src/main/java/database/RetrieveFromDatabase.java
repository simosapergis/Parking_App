package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import objects.ParkingPositionObject;

public class RetrieveFromDatabase {
    private RetrieveFromDatabase(){

    }

    private static final String [] columnsToRetrieve = {
            ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_AREA,
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
        while(cursor.moveToNext()){
            entries.add(fetchObject(cursor));
        }

        cursor.close();
        return entries;
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
        parkingPositionObj.setArea(
                cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA))
        );
        parkingPositionObj.setDatetime(
                cursor.getLong(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME))
        );
         parkingPositionObj.setVehicle(
                 cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE))
         );
        return parkingPositionObj;
    }

}
