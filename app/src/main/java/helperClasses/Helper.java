package helperClasses;


public class Helper {

    private Helper (){

    }

    public static final String TAG ="***Parking App:";
    public static final String PREF_NAME = "USER_PREFERENCES";
    public static final String PREF_EXISTS = "userPreferencesExists";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_LASTSELECTEDLOCALE = "lastSelectedLocale";
    public static final String PREF_VEHICLE = "vehicle";
    public static final String PREF_VEHICLE_ROW = "vehicleSpinnerRow";
    public static final String PREF_VEHICLE_PARKED ="vehicleParked";
    public static final String PREF_VEHICLE_LONGITUDE = "vehicleLongitude";
    public static final String PREF_VEHICLE_LATITUDE = "vehicleLatitude";
    public static final String PREF_ALLOWPARKINGENTRIES =  "allowParkingEntries";
    public static final String PREF_ALLOWCONSOLELOGS =  "allowConsoleLogs";
    public static final String CURRENT_USERNAME = "usename";
    public static final String TEST_DATA_USERNAME = "s_apergis";
    public static final String PARKINGAPP_PROVIDER = "parkingAppProvider";
    public static final String N_A = "N/A";
    public static final String DATE_PATTERN = "dd-MM-yyyy";
    public static final String TIME_PATTERN = "HH:mm";
    public static final String LOCALE_EN = "en_US";
    public static final String LOCALE_EL = "el";
    public static final float BUILDINGS_ZOOM= 20f;
    public static final float STREETS_ZOOM= 15f;
    public static final float CITY_ZOOM= 10f;
    public static final float CONTINENT_ZOOM= 5f;
    public static final float WORLD_ZOOM= 1f;
    public static final float ZERO_ZOOM = 0f;
    public static final int NOTIFICATION_ID = 105;
    public static final int SECONDS = 100;
    public static final int MINUTES = 101;
    public static final short METERS_PER_MINUTE = 0;
    public static final short KMETERS_PER_HOUR = 1;
    public static final String STRING = "String";
    public static final String INT = "Integer";
    public static final String FLOAT = "Float";
    public static final String DOUBLE = "Double";
    public static final String BOOLEAN = "Boolean";
    public static final int PARKING_OPERATION_FAILED = 1000;
    public static final int PARKING_OPERATION_SUCCEEDED = 1001;
    public static final int UNPARKING_OPERATION_FAILED = 2000;
    public static final int UPARKING_OPERATION_SUCCEEDED = 2001;
    public static final int REQUEST_MAP_MARK = 301;
    public enum Scale{
        METETERS_PER_SECOND,
        METERS_PER_MINUTE,
        KMETERS_PER_HOUR
    }

    /*
    String indicating that the user selected to move the camera, to his current location on map
     */
    public static final String GO_TO_CURRENT_LOCATION = "current_location";

    /*
    String indicating that the user selected to move the camera, to the location that the car is parked on map
     */
    public static final String GO_TO_VEHICLE = "vehicle";

    /*
    String id for the notification channel
     */
    public static final String PARKING_CHANNEL = "Parking_Channel";

    /*
    Value for google maps package to use with parking notification intents
     */
    public static final String GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps";

    /*
    Distance from a previous parking position, that user will get notified
     */
    public static final double TARGET_DISTANCE =100.0 ;

    /*
    Filtering Location updates with less than target accuracy below
     */
    public static final float TARGET_ACCURACY = 10f;

    /*
    Considering that Location hasSpeed when value is greater that target speed
     */
    public static final double TARGET_SPEED = 1.0d;

    /*
    Variable to define if logs will be available in console
     */
    public static boolean logsEnabled = true;

}
