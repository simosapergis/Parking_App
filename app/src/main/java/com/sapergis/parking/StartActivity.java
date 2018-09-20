package com.sapergis.parking;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.security.Provider;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import components.ParkingDialog;
import components.PopUp;
import database.ParkingDBHelper;
import helperClasses.CheatSheet;
import helperClasses.FireBaseTestData;
import helperClasses.Helper;
import helperClasses.Location2;
import helperClasses.ParkingPositionFinder;
import interfaces.ParkingDialogInterface;
import interfaces.PopUpInterface;
import objects.ParkingPositionObject;

import static helperClasses.Helper.PREF_ALLOWCONSOLELOGS;
import static helperClasses.Helper.UNPARKING_OPERATION_FAILED;
import static helperClasses.Helper.UPARKING_OPERATION_SUCCEEDED;

public class StartActivity extends ParentActivity implements ActivityCompat.OnRequestPermissionsResultCallback
                                                                , ParkingDialogInterface, PopUpInterface,OnMapReadyCallback{

    private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 seconds */
    private final long FASTEST_INTERVAL = 2000; /* 2 seconds*/
    //private final float LESS_AMOUNT_OF_METERS = 10.0f;
    private final int REQUEST_PREFERENCES_SETUP = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 10;
    private final String NULL_USERNAME = "nullUsername";
    private final String METERS_SIGN = "m";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private ParkingDBHelper parkingDBHelper;
    private PopupWindow popupWindow;
    private TextView distance_text1;
    private TextView distance_text2;
    private TextView speed_text1;
    private TextView speed_text2;
    private boolean vehicleParked;
    private String current_username;
    private Location parkedLocation = null;
    private Location previousLocation;
    private LocationCallback mLocationCallback;
    private GoogleMap map;
    private Marker parkedVehicleMarker = null;
    private Marker currentPositionMarker = null;
    private BitmapDescriptor carBitmap;
    private PopUp mPopUp ;
    private static final boolean TEMPORARYPOSITION = true;
    MarkerOptions userMarkerOptions;
    List<ParkingPositionObject> ppoList = new ArrayList<>();
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        parkingDBHelper = ParkingDBHelper.getParkingDBHelperInstance(this);
        setUpLocationClient();
        setUpViews();
        Log.d(Helper.TAG , "Contains? "+(getAppSharedPreferences().contains(Helper.PREF_EXISTS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //localeTest("el");
        if(!getAppSharedPreferences().contains(Helper.PREF_EXISTS)){
            setUpPreferences();
        }else{
            if(!getAppSharedPreferences().getBoolean(PREF_ALLOWCONSOLELOGS, true)){
                Helper.logsEnabled = false;
            }
            current_username = getAppSharedPreferences().getString(Helper.PREF_USERNAME , NULL_USERNAME);
            checkPermissions();
            startLocationUpdates();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFusedLocationClient!=null && mLocationCallback!=null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * method to register for location updates
     */
    protected void setUpLocationClient() {
        previousLocation = null;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(LESS_AMOUNT_OF_METERS);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_start_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.goToSettings:
                Intent intent = new Intent(StartActivity.this, SetupConfigActivity.class);
                startActivity(intent);
                return true;

            case R.id.add_data:
                retrieveFromFirebase();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * method to start location updates
     */
    protected void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Helper.logsEnabled){
                Log.d(Helper.TAG, "checkSelfPermission");
            }
            return;
        }
        if (mLocationCallback==null){
            mLocationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    //if(locationResult.getLastLocation().getAccuracy()< Helper.TARGET_SPEED){
                        onLocationChanged(locationResult.getLastLocation());
                   // }

                }
            };
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    /**
     *method to set up the UI elements of the activity
     */
    private void setUpViews(){
        Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPopUp==null){
                    mPopUp = new PopUp(v, StartActivity.this);
                }
                if(mPopUp.isShowing()){
                   mPopUp.dismiss();
                }else{
                    mPopUp.show();
                }
            }
        });
        FloatingActionButton getParkingPos;
        FloatingActionButton stats;
        SupportMapFragment mapFragment;
        BitmapDescriptor locationBitmap;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getParkingPos = findViewById(R.id.getParkingPosBtn);
        getParkingPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeOrRetrievePosition();
            }
        });
        getParkingPos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(vehicleParked){
                    ParkingDialog parkingDialog = new ParkingDialog();
                    parkingDialog.show(StartActivity.this,
                            getResources().getString(R.string.release_parking_position),
                            getResources().getString(R.string.yes),
                            getResources().getString(R.string.no));
                    return true;
                }
                return false;
            }
        });
        stats = findViewById(R.id.statsBtn);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this , ParkingStatisticsActivity.class);
                intent.putExtra(Helper.CURRENT_USERNAME , current_username);
                startActivity(intent);
            }
        });
        distance_text1 = (TextView)findViewById(R.id.distance_text);
        distance_text2 = (TextView)findViewById(R.id.distance_text2);
        speed_text1 = (TextView)findViewById(R.id.moving_speed_text);
        speed_text2 = (TextView)findViewById(R.id.moving_speed_text2);
        carBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_parked_car);
        locationBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_current_location_3);
        userMarkerOptions = new MarkerOptions()
                .title(getResources().getString(R.string.your_location))
                .icon(locationBitmap)
                ;
    }

    /**
     * Method to set the intent for the setUpConfig Activity, when the app loads for the first time
     */
    private void setUpPreferences(){
        Intent preferencesIntent = new Intent(StartActivity.this, SetupConfigActivity.class);
        startActivityForResult(preferencesIntent , REQUEST_PREFERENCES_SETUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PREFERENCES_SETUP && resultCode==RESULT_OK) {
            checkPermissions();
        }
    }

    /**
     *method to store or retrieve the position of the vehicle
     */
    private void storeOrRetrievePosition(){
      new ParkingServiceTask().execute(vehicleParked);
    }

    /**
     *method to get & store the location that the user parked the vehicle
     */
//    private void getParkingLocation() {
//        final long resultSuccess = 1;
//        final long resultFailure = -1;
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if(Helper.logsEnabled){
//                Log.d(Helper.TAG, "Entered Permissions check");
//            }
//
//            if(!checkPermissions()){
//                return resultFailure;
//            }
//        }
//        Task<Location> task =  mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        long result = -1;
//                        if(location !=null){
//                            if(Helper.logsEnabled){
//                                Log.d(Helper.TAG, "Location is "+location);
//                            }
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put(Helper.PREF_VEHICLE_PARKED, true);
//                            hashMap.put(Helper.PREF_VEHICLE_LONGITUDE, String.valueOf(location.getLongitude()));
//                            hashMap.put(Helper.PREF_VEHICLE_LATITUDE, String .valueOf(location.getLatitude()));
//                            result = storeParkingLocation(location);
//                            if (result >= 0){
//                                updateSharedPreferences(hashMap);
//
//                            }
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_on_getting_gps_last_loc) , Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//        });
//    }

    private Location getParkingLocation() {
        Task<Location> task = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Helper.logsEnabled){
                Log.d(Helper.TAG, "Entered Permissions check");
            }

            if(!checkPermissions()){
                return null;
            }
        }
        task =  mFusedLocationClient.getLastLocation();
        while(!task.isComplete()){

        }
        return task.getResult();
    }

    private long prepareHashMapForLocationStoring(Location location){
        long result = -1;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Helper.PREF_VEHICLE_PARKED, true);
        hashMap.put(Helper.PREF_VEHICLE_LONGITUDE, String.valueOf(location.getLongitude()));
        hashMap.put(Helper.PREF_VEHICLE_LATITUDE, String .valueOf(location.getLatitude()));
        result = storeParkingLocation(location);
        if (result >= 0){
            updateSharedPreferences(hashMap);
            vehicleParked = true;
            result = 1;
        }
        return result;
    }
        /**
         * method to store the location that the user parked the vehicle
         */
    private long storeParkingLocation(Location location){
        long result = -1;
        //Locale locale = new Locale(getResources().getString(R.string.en_US));
        Locale locale = Locale.getDefault();
        final int maxresults = 1;
        List<Address> addressesList = null;
        Address address = null;
        String area = null;
        String parked_address = null;
        String parked_address_no = null;
        Geocoder geocoder = new Geocoder(getBaseContext(), locale);
        Calendar calendar = Calendar.getInstance();
        long datetime = calendar.getTimeInMillis();
        try{
           addressesList = geocoder.getFromLocation(location.getLatitude() , location.getLongitude() , maxresults);
           address = addressesList.get(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        area = (address == null ? Helper.N_A :  address.getLocality());
        parked_address = (address == null ? Helper.N_A :  address.getThoroughfare());
        parked_address_no = (address == null ? Helper.N_A :  address.getSubThoroughfare());
        ParkingPositionObject parkingPositionObj = new ParkingPositionObject();
        parkingPositionObj.setUsername(current_username);
        parkingPositionObj.setLatitude(location.getLatitude());
        parkingPositionObj.setLongitude(location.getLongitude());
        parkingPositionObj.setArea(area);
        parkingPositionObj.setAddress_parked(parked_address);
        parkingPositionObj.setParked_address_no(parked_address_no);
        parkingPositionObj.setDatetime(datetime);
        parkingPositionObj.setVehicle(getAppSharedPreferences().getString(Helper.PREF_VEHICLE, null));
        if(Helper.logsEnabled){
            Log.d(Helper.TAG , "datetime -> "+datetime +" area -> "+ area +" address -> "+parked_address +" no-> "+parked_address_no);
        }
        parkedLocation = location;
        result = parkingDBHelper.storeParkingPosition(parkingPositionObj, TEMPORARYPOSITION);
//        if(result > -1){
//            showParkedVehiclePositionOnMap(parkedLocation);
//        }
        //vehicleParked = true;
        //Toast.makeText(this,R.string.vehicle_parked, Toast.LENGTH_LONG).show();
        return result;
    }

    /**
     * method to get location changes passed from requestLocationUpdates.
     */
    private void onLocationChanged(Location location){
        Location2 location2 =new Location2(location,previousLocation, Helper.Scale.METETERS_PER_SECOND);
        /*
        Below, we need to show user's current location on map when starting the app, and to check that, this is true when previousLocation is null.
         After that we need to refresh the position on map, only when the user is moving.
         */
        if(location2.hasSpeed() || previousLocation==null){
            showUserPositionMarkerToMap(location2);
        }

        Float distanceFromVehicle = null;
        if(Helper.logsEnabled){
            Log.d(Helper.TAG,"Location Changed To -> lat:"+ location2.getLatitude()+ " lon:"+ location2.getLongitude() +" acc:"+ location.getAccuracy());
        }
        if (previousLocation!=null){
            speed_text2.setText(String.valueOf(location2.getSpeed()));
           Log.d(Helper.TAG, "potential speed is "+location2.getSpeed() +
                   " based on "+location2.distanceTo(previousLocation)+"meters distance from previous location");
        }
        previousLocation = location2;
        if(vehicleParked){
            distanceFromVehicle = location.distanceTo(parkedLocation);
            sb = new StringBuilder();
            sb.append(String.format(Locale.getDefault(),"%.2f",distanceFromVehicle)).append(METERS_SIGN);
            distance_text2.setText(sb);
            if(Helper.logsEnabled){
                Log.d(Helper.TAG , "Distance from your vehicle -> "+distanceFromVehicle);
            }
            showDistanceText();
        }else{
            CCtest();
        }

    }

    private void showParkedVehiclePositionOnMap(Location location){
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        parkedLocation = location;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        parkedVehicleMarker = map.addMarker(new MarkerOptions().position(currentLocation)
                .title(getResources().getString(R.string.parked_vehicle_marker)));
        parkedVehicleMarker.setIcon(carBitmap);
        parkedVehicleMarker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                currentLocation, Helper.STREETS_ZOOM));
        vehicleParked = true;
    }
    /**
     * method to check if location permissions are granted
     */
    private boolean checkPermissions(){
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            return true;
        }else{
            requestPermissions();
            return false;
        }
    }

    /**
     * method to request location permissions on runtime
     */
    private void requestPermissions(){
        String [] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, permission, REQUEST_ACCESS_FINE_LOCATION );
    }

    @Override
    public void releaseParkingLocation(boolean release) {
        if(release){
            releaseParkingObjects();
            Toast.makeText(this, R.string.parking_pos_released, Toast.LENGTH_LONG).show();

        }
    }

    private void retrieveFromFirebase(){
        FireBaseTestData testDataList = new FireBaseTestData(this);
        testDataList.requestTestListFromFireBase();
        current_username = Helper.TEST_DATA_USERNAME;
    }

    /**
     * method to upload parking location enties for testing
     */
    private void uploadToFirebase(){
        List<ParkingPositionObject> entries = null;
        entries = parkingDBHelper.retrieveAllParkingEntries(current_username);
        FireBaseTestData testDataList = new FireBaseTestData(this);
        testDataList.postTestListToFireBase(entries);
    }

    private void showDistanceText(){
        if( ! (distance_text1.getVisibility()== View.VISIBLE  && distance_text2.getVisibility() == View.VISIBLE) ){
            distance_text1.setVisibility(View.VISIBLE);
            distance_text2.setVisibility(View.VISIBLE);
        }
    }

    private void hideDistanceText(){
        if( distance_text1.getVisibility()== View.VISIBLE  && distance_text2.getVisibility() == View.VISIBLE){
            distance_text1.setVisibility(View.GONE);
            distance_text2.setVisibility(View.GONE);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(getAppSharedPreferences().getBoolean(Helper.PREF_VEHICLE_PARKED, false)){
            restoreLastParkedPosition();
        }
    }

    private void restoreLastParkedPosition(){
        String longitude = getAppSharedPreferences().getString(Helper.PREF_VEHICLE_LONGITUDE, null);
        String latitude = getAppSharedPreferences().getString(Helper.PREF_VEHICLE_LATITUDE, null);
        if(longitude!=null && latitude!=null){
            Location location = new Location(Helper.PARKINGAPP_PROVIDER);
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
            showParkedVehiclePositionOnMap(location);
        }
    }
    //*****needs fix
    private void CCtest(){
        ppoList.clear();
        ppoList = parkingDBHelper.retrieveAllParkingEntries(current_username);
        ParkingPositionFinder ppf = new ParkingPositionFinder(this);
        Location location = new Location(Helper.PARKINGAPP_PROVIDER);
        location.setLatitude(37.8886672);
        location.setLongitude(23.7524711);
        ppf.searchForPossibleParkingPlaces(location, ppoList);
    }

    private void releaseParkingObjects(){
        vehicleParked = false;
        parkedLocation = null;
        rmParkingPosFromSharedPref();
        if (parkedVehicleMarker != null) {
            parkedVehicleMarker.remove();
        }
        hideDistanceText();
    }

    private void rmParkingPosFromSharedPref(){
        getAppSharedPreferences().edit().remove(Helper.PREF_VEHICLE_PARKED).apply();
        getAppSharedPreferences().edit().remove(Helper.PREF_VEHICLE_LONGITUDE).apply();
        getAppSharedPreferences().edit().remove(Helper.PREF_VEHICLE_LATITUDE).apply();
    }

    private void showUserPositionMarkerToMap(Location location){
        if(currentPositionMarker!=null) {
            currentPositionMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        userMarkerOptions.position(latLng);
        currentPositionMarker = map.addMarker(userMarkerOptions);
        currentPositionMarker.showInfoWindow();
        moveCameraTo(location);
    }

    private void moveCameraTo(Location location){
        if (location!=null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Helper.STREETS_ZOOM));
        }

    }

    private void localeTest (String language){
        Log.d(Helper.TAG , "Default Locale = "+ Locale.getDefault());
      Configuration config  = getResources().getConfiguration();
        if(Build.VERSION.SDK_INT >= 17) {
            config.setLocale(new Locale(language));
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Resources resources = new Resources(getAssets(),metrics,config);
            String str = resources.getString(R.string.parked_at);
            System.out.println("Simos ");
        }
//        Context context = getApplicationContext();
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//        System.out.println("**Simos - deault locale : "+Locale.getDefault()+" --");
//        Resources resources = context.getResources();
//        Configuration config  = new Configuration(resources.getConfiguration());
//        if(Build.VERSION.SDK_INT >= 17){
//            config.setLocale(locale);
//            context.createConfigurationContext(config);
//        }else{
//            config.locale = locale;
//            resources.updateConfiguration(config, resources.getDisplayMetrics());
//        }

    }

    @Override
    public void popUpSelectionIs(String selection) {
        if (selection.equalsIgnoreCase(Helper.GO_TO_CURRENT_LOCATION)) {
            moveCameraTo(previousLocation);
        }else if(selection.equalsIgnoreCase(Helper.GO_TO_VEHICLE) && parkedLocation!=null){
            moveCameraTo(parkedLocation);
        }else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_parked_vehicle), Toast.LENGTH_LONG).show();
        }
    }

    class ParkingServiceTask extends AsyncTask<Boolean, Void, Integer>{

        @Override
        protected Integer doInBackground(Boolean... vehicleParked) {
           try{

                /*
            Case where we reach the parked vehicle
             */
               if(vehicleParked[0]){
                   long result = -1;
                   ParkingPositionObject parkingPositionObject = parkingDBHelper.retrieveParkingPosition(current_username, TEMPORARYPOSITION);
                   if(getAppSharedPreferences().getBoolean(Helper.PREF_ALLOWPARKINGENTRIES, false) && parkingPositionObject!=null){
                       parkingDBHelper.storeParkingPosition(parkingPositionObject, !TEMPORARYPOSITION);
                       result = parkingDBHelper.deleteTempPosition(getApplicationContext());
                   }
                   if(result>0){
                       return Helper.UPARKING_OPERATION_SUCCEEDED;
                   }else{
                       return  Helper.UNPARKING_OPERATION_FAILED;
                   }

               }
            /*
            case when we just parked the vehicle
             */
               else{
                   Location currentLocation = getParkingLocation();
                   if(currentLocation!=null){
                       return prepareHashMapForLocationStoring(currentLocation) >= 1 ?
                               Helper.PARKING_OPERATION_SUCCEEDED :
                               Helper.PARKING_OPERATION_FAILED;
                   }
                   return  Helper.PARKING_OPERATION_FAILED;
               }

           }
           catch(Exception ex){
               Log.e(Helper.TAG, ex.getLocalizedMessage());
               return 0;
           }

        }

        @Override
        protected void onPostExecute(Integer result) {

            switch (result){
                case Helper.UPARKING_OPERATION_SUCCEEDED:
                    Toast.makeText(getApplicationContext(), R.string.parked_vehicle_reached, Toast.LENGTH_LONG).show();
                    releaseParkingObjects();
                    break;
                case Helper.UNPARKING_OPERATION_FAILED:
                    Toast.makeText(getApplicationContext(),  R.string.error_deleting_last_loc_from_db,Toast.LENGTH_LONG).show();
                    break;
                case Helper.PARKING_OPERATION_SUCCEEDED:
                    showParkedVehiclePositionOnMap(parkedLocation);
                    Toast.makeText(getApplicationContext(),R.string.vehicle_parked, Toast.LENGTH_LONG).show();
                    break;
                case Helper.PARKING_OPERATION_FAILED:
                    Toast.makeText(getApplicationContext(),  R.string.error_on_getting_gps_last_loc ,Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(),  R.string.general_error ,Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

}
