package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.sapergis.parking.R;

import java.util.ArrayList;
import java.util.List;

import helperClasses.Helper;
import objects.ParkingPositionObject;


public class ParkingDBHelper extends SQLiteOpenHelper {
    private static ParkingDBHelper instance;
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "ParkingDatabase";
    private static final String DROP_TABLE_IF_EXISTS= "DROP TABLE IF EXISTS ";
    private Context sContext;
    private static final String [] columnsToRetrieve = {
            ParkingLocationDBContract.ParkingLocation._ID,
            ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE,
            ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME,
            ParkingLocationDBContract.ParkingLocation.COLUMN_AREA,
            ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED,
            ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED_NO,
            ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE
    };

    public static synchronized ParkingDBHelper getParkingDBHelperInstance(Context context){
        if(instance==null){
            instance= new ParkingDBHelper(context);
        }
        return instance;
    }

    public ParkingDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sContext = context;
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

    /*
    CRUD operations below : Store - Retrieve - Delete
     */

    public long storeParkingPosition(ParkingPositionObject ppo, boolean temporaryPosition){
        long result = -1;
        SQLiteDatabase database = instance.getWritableDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            if(temporaryPosition){
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_USERNAME, ppo.getUsername());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_LATITUDE , ppo.getLatitude());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_LONGITUDE , ppo.getLongitude());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_DATETIME , ppo.getDatetime());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_AREA , ppo.getArea());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_ADDRESS_PARKED , ppo.getParked_address());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_ADDRESS_PARKED_NO , ppo.getParked_address_no());
                values.put(ParkingLocationDBContract.TempParkingLocation.COLUMN_VEHICLE , ppo.getVehicle());
                result = database.insert(ParkingLocationDBContract.TempParkingLocation.TABLE_NAME , null, values);
                database.setTransactionSuccessful();
            }else{
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME, ppo.getUsername());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE , ppo.getLatitude());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE , ppo.getLongitude());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME , ppo.getDatetime());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA , ppo.getArea());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED , ppo.getParked_address());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED_NO , ppo.getParked_address_no());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE , ppo.getVehicle());
                result = database.insert(ParkingLocationDBContract.ParkingLocation.TABLE_NAME , null, values);
                database.setTransactionSuccessful();
            }
            return result;
        }
        catch(Exception ex){
            printErrorsToConsole(ex ,sContext.getResources().getString(R.string.error_on_store));
        }
        finally {
            database.endTransaction();
        }
        return result;
    }

    public  void storeFirebaseTestData(List<ParkingPositionObject> entries){
        SQLiteDatabase database = instance.getWritableDatabase();
        database.beginTransaction();
        try{
            for (ParkingPositionObject ppo : entries){
                ContentValues values = new ContentValues();
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME, ppo.getUsername());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LATITUDE , ppo.getLatitude());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_LONGITUDE , ppo.getLongitude());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_DATETIME , ppo.getDatetime());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_AREA , ppo.getArea());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED , ppo.getParked_address());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_ADDRESS_PARKED_NO , ppo.getParked_address_no());
                values.put(ParkingLocationDBContract.ParkingLocation.COLUMN_VEHICLE , ppo.getVehicle());
                long result = database.insert(ParkingLocationDBContract.ParkingLocation.TABLE_NAME , null, values);
            }
            database.setTransactionSuccessful();
        }catch(Exception ex){
            printErrorsToConsole(ex ,sContext.getResources().getString(R.string.error_on_store));
        }finally {
            database.endTransaction();
        }

    }
    public ParkingPositionObject retrieveParkingPosition(String userName, boolean temporaryPosition){
        ParkingPositionObject parkingPositionObj;
        SQLiteDatabase database = instance.getReadableDatabase();
        String selection = temporaryPosition ?
                ParkingLocationDBContract.TempParkingLocation.COLUMN_USERNAME +" = ?" :
                ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME +"=?" ;
        String [] selectionArgs = {userName};
        String tableName = temporaryPosition ?
                ParkingLocationDBContract.TempParkingLocation.TABLE_NAME :
                ParkingLocationDBContract.ParkingLocation.TABLE_NAME ;
        Cursor cursor = database.query(
                tableName,
                columnsToRetrieve,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        parkingPositionObj=fetchObject(cursor, temporaryPosition);

        cursor.close();
        return parkingPositionObj;
    }


    public List<ParkingPositionObject> retrieveAllParkingEntries (String ...userNames){
        ArrayList<ParkingPositionObject> allParkingEntries = new ArrayList<>();
        SQLiteDatabase database = instance.getReadableDatabase();
        String selection = ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME +" = ?";
        for(String userName : userNames){
            String [] selectionArgs = {userName};
            Cursor cursor = database.query(
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
                allParkingEntries.add(fetchObject(cursor, false));
                counter++;
                cursor.moveToNext();
            }

            cursor.close();
        }

        return allParkingEntries;
    }
    public long deleteTempPosition(Context context){
        long result = -1;
        SQLiteDatabase database = instance.getWritableDatabase();
        database.beginTransaction();
        try{
            result = database.delete(ParkingLocationDBContract.TempParkingLocation.TABLE_NAME, null, null  );
            database.setTransactionSuccessful();
        }catch(Exception ex){
            Log.e(Helper.TAG, sContext.getResources().getString(R.string.error_on_delete));
            Log.e(Helper.TAG, ex.toString());
        }finally{
            database.endTransaction();
        }
        return result;
    }

    /*
    Method to set data to the Model from the database resultset
     */
    private static ParkingPositionObject fetchObject(Cursor cursor, Boolean isTemporary){
        ParkingPositionObject parkingPositionObj = new ParkingPositionObject();

        if(isTemporary){
            parkingPositionObj.setId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation._ID))
            );
            parkingPositionObj.setUsername(
                    cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_USERNAME))
            );
            parkingPositionObj.setLongitude(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_LONGITUDE))
            );
            parkingPositionObj.setLatitude(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_LATITUDE))
            );
            parkingPositionObj.setDatetime(
                    cursor.getLong(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_DATETIME))
            );
            parkingPositionObj.setArea(
                    cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_AREA))
            );
            parkingPositionObj.setAddress_parked(
                    cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_ADDRESS_PARKED))
            );
            parkingPositionObj.setParked_address_no(
                    cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_ADDRESS_PARKED_NO))
            );
            parkingPositionObj.setVehicle(
                    cursor.getString(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.TempParkingLocation.COLUMN_VEHICLE))
            );
        }else{
            parkingPositionObj.setId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ParkingLocationDBContract.ParkingLocation._ID))
            );
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
        }

        return parkingPositionObj;
    }

    public List<String> retrieveStatistics (String ...userNames) {
        SQLiteDatabase readableDatabase = instance.getReadableDatabase();
        readableDatabase.beginTransaction();
        ArrayList<String> statistics = new ArrayList<>();
        String selection = ParkingLocationDBContract.ParkingLocation.COLUMN_USERNAME + " = ?";
        String column = ParkingLocationDBContract.ParkingLocation.COLUMN_AREA;


        try{
            for(String userName : userNames) {
                String[] selectionArgs = {userName};
                Cursor cursor = readableDatabase.query(
                        true,
                        ParkingLocationDBContract.ParkingLocation.TABLE_NAME,
                        columnsToRetrieve, selection, selectionArgs, column, null, null, null
                );

                cursor.moveToFirst();
                int counter = 0;
                int count = cursor.getCount();
                while (counter < count) {
                    statistics.add(cursor.getString(cursor.getColumnIndexOrThrow(column)));
                    counter++;
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }catch(Exception ex){
            Log.e(Helper.TAG, ex.toString());
        }finally {
            readableDatabase.endTransaction();
        }

        return statistics;
    }

    private void printErrorsToConsole(Exception exception , String description){
        Log.e(Helper.TAG, description+"\n"+exception.toString());
    }
}
