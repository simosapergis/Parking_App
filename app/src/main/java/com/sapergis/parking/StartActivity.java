package com.sapergis.parking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import components.ParkingDialog;
import database.ParkingDBHelper;
import helperClasses.FireBaseTestData;
import helperClasses.Helper;
import helperClasses.ParkingPositionFinder;
import interfaces.ParkingDialogInterface;
import objects.ParkingPositionObject;

import static helperClasses.Helper.PREF_ALLOWCONSOLELOGS;

public class StartActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
                                                                , ParkingDialogInterface, OnMapReadyCallback{

    private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 seconds */
    private final long FASTEST_INTERVAL = 2000; /* 2 seconds*/
    //private final float LESS_AMOUNT_OF_METERS = 10.0f;
    private final int REQUEST_PREFERENCES_SETUP = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 10;
    private final String TEST_DATA_USERNAME = "s_apergis";
    private final String NULL_USERNAME = "nullUsername";
    private final String METERS_SIGN = "m";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private ParkingDBHelper parkingDBHelper;
    private TextView distance_text1;
    private TextView distance_text2;
    private boolean vehicleParked;
    private String current_username;
    SharedPreferences sharedPreferences;
    private Location parkedLocation = null;
    private LocationCallback mLocationCallback;
    private GoogleMap map;
    private Marker parkedVehicleMarker, currentPositionMarker = null;
    private BitmapDescriptor carBitmap;
    private static final boolean TEMPORARYPOSITION = true;
    MarkerOptions userMarkerOptions;
    List<ParkingPositionObject> ppoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        parkingDBHelper = ParkingDBHelper.getParkingDBHelperInstance(this);
        setUpLocationClient();
        setUpViews();
        sharedPreferences = getSharedPreferences(Helper.PREF_NAME, 0);
        Log.d(Helper.TAG , "Contains? "+(sharedPreferences.contains(Helper.PREF_EXISTS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //localeTest("el");
        if(!sharedPreferences.contains(Helper.PREF_EXISTS)){
            setUpPreferences();
        }else{
            if(!sharedPreferences.getBoolean(PREF_ALLOWCONSOLELOGS, true)){
                Helper.logsEnabled = false;
            }
            current_username = sharedPreferences.getString(Helper.PREF_USERNAME , NULL_USERNAME);
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
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    /**
     *method to set up the UI elements of the activity
     */
    private void setUpViews(){
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
                    if(Helper.logsEnabled){
                        Log.d(Helper.TAG , "LONG CLICKED");
                    }
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
        ParkingPositionObject parkingPositionObject = null;
        try{
            if(vehicleParked){
                parkingPositionObject = parkingDBHelper.retrieveParkingPosition(current_username, TEMPORARYPOSITION);
                if(sharedPreferences.getBoolean(Helper.PREF_ALLOWPARKINGENTRIES, false)){
                    parkingDBHelper.storeParkingPosition(parkingPositionObject, !TEMPORARYPOSITION);
                }
                parkingDBHelper.deleteTempPosition(this);
                Toast.makeText(this, R.string.parked_vehicle_reached, Toast.LENGTH_LONG).show();
                vehicleParked = false;
            }else{
                getParkingLocation();

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     *method to get & store the location that the user parked the vehicle
     */
    private void getParkingLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(Helper.logsEnabled){
                Log.d(Helper.TAG, "Entered Permissions check");
            }

            if(!checkPermissions()){
                return;
            }
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location !=null){
                            if(Helper.logsEnabled){
                                Log.d(Helper.TAG, "Location is "+location);
                            }
                            storeParkingLocation(location);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),  getResources().getString(R.string.error_on_getting_gps_last_loc) , Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    /**
     * method to store the location that the user parked the vehicle
     */
    private void storeParkingLocation(Location location){
        Locale locale = new Locale(getResources().getString(R.string.en_US));
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
        parkingPositionObj.setVehicle(sharedPreferences.getString(Helper.PREF_VEHICLE, null));
        if(Helper.logsEnabled){
            Log.d(Helper.TAG , "datetime -> "+datetime +" area -> "+ area +" address -> "+parked_address +" no-> "+parked_address_no);
        }
        parkedLocation = location;
        showParkedVehiclePositionOnMap(parkedLocation);
        parkingDBHelper.storeParkingPosition(parkingPositionObj, TEMPORARYPOSITION);
        vehicleParked = true;
        Toast.makeText(this,R.string.vehicle_parked, Toast.LENGTH_LONG).show();
    }

    /**
     * method to get location changes passed from requestLocationUpdates.
     */
    private void onLocationChanged(Location location){
        if(currentPositionMarker!=null){
            currentPositionMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        userMarkerOptions.position(latLng);
        currentPositionMarker = map.addMarker(userMarkerOptions);
        currentPositionMarker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, Helper.STREETS_ZOOM));
        Float distanceFromVehicle = null;
        if(Helper.logsEnabled){
            Log.d(Helper.TAG,"Location Changed To -> lat:"+ location.getLatitude()+ " lon:"+ location.getLongitude() +" acc:"+ location.getAccuracy());
        }
        if(vehicleParked){
            distanceFromVehicle = location.distanceTo(parkedLocation);
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("%.2f",distanceFromVehicle)).append(METERS_SIGN);
            distance_text2.setText(sb);
            if(Helper.logsEnabled){
                Log.d(Helper.TAG , "Distance from your vehicle -> "+distanceFromVehicle);
            }
            showDistanceText();
        }else {
            hideDistanceText();
            if (parkedVehicleMarker != null) {
                parkedVehicleMarker.remove();
            }
        }
        test();
    }

    private void showParkedVehiclePositionOnMap(Location location){
        LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());
        map.getUiSettings().setMyLocationButtonEnabled(true);
        parkedVehicleMarker = map.addMarker(new MarkerOptions().position(current_location)
                .title(getResources().getString(R.string.parked_vehicle_marker)));
        parkedVehicleMarker.setIcon(carBitmap);
        parkedVehicleMarker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                current_location, Helper.STREETS_ZOOM));
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
            parkingDBHelper.deleteTempPosition(getBaseContext());
            Toast.makeText(this, R.string.parking_pos_released, Toast.LENGTH_LONG).show();
            vehicleParked = false;
        }
    }

    private void retrieveFromFirebase(){
        FireBaseTestData testDataList = new FireBaseTestData(this);
        testDataList.requestTestListFromFireBase();
        current_username = TEST_DATA_USERNAME;
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

    }

    private void test(){
        ppoList.clear();
        ppoList = parkingDBHelper.retrieveAllParkingEntries(current_username);
        ParkingPositionFinder ppf = new ParkingPositionFinder(this);
        Location location = new Location("testLoc");
        location.setLatitude(37.8650433);
        location.setLongitude(23.756);
        ppf.searchForPossibleParkingPlaces(location, ppoList);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

//    @NonNull Resources getLocalizedResources (Context context,Locale newLocale){
//        Configuration config = context.getResources().getConfiguration();
//
//    }

}
