package helperClasses;


public class Helper {

    private Helper (){

    }

    public static final String TAG ="***Parking App:";
    public static final String PREF_NAME = "USER_PREFERENCES";
    public static final String PREF_EXISTS = "userPreferencesExists";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_VEHICLE = "vehicle";
    public static final String PREF_VEHICLE_ROW = "vehicleSpinnerRow";
    public static final String PREF_ALLOWPARKINGENTRIES =  "allowParkingEntries";
    public static final String PREF_ALLOWCONSOLELOGS =  "allowConsoleLogs";
    public static final String CURRENT_USERNAME = "usename";
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

    /*
    Distance from a previous parking position, that user will get notified
     */
    public static final double TARGET_DISTANCE =100.0 ;

    /*
    Variable to define if logs will be available in console
     */
    public static boolean logsEnabled = true;

}
