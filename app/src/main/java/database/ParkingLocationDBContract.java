package database;

import android.provider.BaseColumns;

public final class ParkingLocationDBContract {
    public ParkingLocationDBContract (){

    }

    public static class ParkingLocation implements BaseColumns{
        public static final String TABLE_NAME = "parking_location";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_DATETIME = "datetime";
        public static final String COLUMN_AREA  = "area";
        public static final String COLUMN_VEHICLE  = "vehicle";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +" ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_USERNAME + " TEXT, "+
                COLUMN_LATITUDE + " REAL, "+
                COLUMN_LONGITUDE + " REAL, "+
                COLUMN_DATETIME + " INTEGER, "+
                COLUMN_AREA + " TEXT, " +
                COLUMN_VEHICLE +" TEXT )";
    }

    public static class TempParkingLocation implements BaseColumns{
        public static final String TABLE_NAME = "temp_parking_location";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_DATETIME = "datetime";
        public static final String COLUMN_AREA  = "area";
        public static final String COLUMN_VEHICLE  = "vehicle";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +" ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_USERNAME + " TEXT, "+
                COLUMN_LATITUDE + " REAL, "+
                COLUMN_LONGITUDE + " REAL, "+
                COLUMN_DATETIME + " INTEGER, "+
                COLUMN_AREA + " TEXT, " +
                COLUMN_VEHICLE +" TEXT )";
    }


}
