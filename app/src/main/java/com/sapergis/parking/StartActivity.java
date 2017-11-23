package com.sapergis.parking;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import components.ParkingDialog;
import database.DeleteFromDatabase;
import database.ParkingDBHelper;
import database.RetrieveFromDatabase;
import database.StoreToDatabase;
import helperClasses.Helper;
import interfaces.ParkingDialogInterface;
import objects.ParkingPositionObject;

public class StartActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
                                                                , ParkingDialogInterface{

    private final long UPDATE_INTERVAL = 10 * 1000;  /* 10 seconds */
    private final long FASTEST_INTERVAL = 2000; /* 2 seconds*/
    //private final float LESS_AMOUNT_OF_METERS = 10.0f;

    private final int REQUEST_PREFERENCES_SETUP = 1;
    private final int REQUEST_ACCESS_FINE_LOCATION = 10;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private ImageButton getParkingPos;
    private Button config;
    private Button stats;
    private TextView distance_text1;
    private TextView distance_text2;
    private boolean vehicleParked;
    private String current_username;
    private final String NULL_USERNAME = "nullUsername";
    SharedPreferences sharedPreferences;
    private Location parkedLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initiateLocationUpdates();
        setUpViews();
        sharedPreferences = getSharedPreferences(Helper.PREF_NAME, 0);
        Log.d(Helper.TAG , "Contains? "+(sharedPreferences.contains(Helper.PREF_EXISTS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!sharedPreferences.contains(Helper.PREF_EXISTS)){
            setUpPreferences();
        }else{
            current_username = sharedPreferences.getString(Helper.PREF_USERNAME , NULL_USERNAME);
        }
    }

    /**
     * method to register for location updates
     */
    protected void initiateLocationUpdates() {
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(Helper.TAG, "checkSelfPermission");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    /**
     *method to set up the UI elements of the activity
     */
    private void setUpViews(){
        getParkingPos = (ImageButton) findViewById(R.id.getParkingPos);
        getParkingPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeOrRetrievePosition();
            }
        });
        getParkingPos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ParkingDialog parkingDialog = new ParkingDialog();
                parkingDialog.show(StartActivity.this,
                        getResources().getString(R.string.release_parking_position),
                        getResources().getString(R.string.yes),
                        getResources().getString(R.string.no));
                Log.d(Helper.TAG , "LONG CLICKED");
                return true;
            }
        });
        config = (Button) findViewById(R.id.configBtn);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, SetupConfigActivity.class);
                startActivity(intent);
            }
        });
        stats = (Button)findViewById(R.id.statsBtn);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this , ParkingStatistics.class);
                intent.putExtra(Helper.CURRENT_USERNAME , current_username);
                startActivity(intent);
            }
        });
        distance_text1 = (TextView)findViewById(R.id.distance_text);
        distance_text2 = (TextView)findViewById(R.id.distance_text2);
    }

    /**
     * Method to set the intent for the setUpConfig Activity, when the app loads for the first time
     */
    private void setUpPreferences(){
        Intent preferencesIntent = new Intent(StartActivity.this, SetupConfigActivity.class);
        startActivity(preferencesIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // if(requestCode == REQUEST_PREFERENCES_SETUP && resultCode==RESULT_OK){
            //Intent resultIntent = getIntent();
            //resultIntent.getStringExtra(")
        //}
    }

    /**
     *method to store or retrieve the position of the vehicle
     */
    private void storeOrRetrievePosition(){
        ParkingPositionObject parkingPositionObject = null;
        try{
            if(vehicleParked){
                SQLiteDatabase readableDatabase = openReadableDatabase();
                parkingPositionObject = RetrieveFromDatabase.retrieveTempPosition(readableDatabase ,current_username);
                closeReadableDatabase(readableDatabase);
                SQLiteDatabase writableDatabase = openWritableDatabase();
                if(sharedPreferences.getBoolean(Helper.PREF_ALLOWPARKINGENTRIES, false)){
                    StoreToDatabase.storePosition(parkingPositionObject , writableDatabase);
                }
                DeleteFromDatabase.deleteTempPosition(this);
                closeWritableDatabase(writableDatabase);
                vehicleParked = false;
            }else{
                getParkingLocation();
                vehicleParked = true;
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
            Log.d(Helper.TAG, "Entered Permissions check");
            if(!checkPermissions()){
                return;
            }
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location !=null){
                            Log.d(Helper.TAG, "Location is "+location);
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
        Geocoder geocoder = new Geocoder(getBaseContext(), locale);
        long datetime = Calendar.getInstance().getTimeInMillis();
        try{
           addressesList = geocoder.getFromLocation(location.getLatitude() , location.getLongitude() , maxresults);
           address = addressesList.get(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        area = (address == null ? "N/A" :  address.getLocality());
        ParkingPositionObject parkingPositionObj = new ParkingPositionObject();
        parkingPositionObj.setUsername(current_username);
        parkingPositionObj.setLatitude(location.getLatitude());
        parkingPositionObj.setLongitude(location.getLongitude());
        parkingPositionObj.setArea(area);
        parkingPositionObj.setDatetime(datetime);
        Log.d(Helper.TAG , "datetime -> "+datetime +" area -> "+ area);
        parkedLocation = location;
        SQLiteDatabase writableDatabase = openWritableDatabase();
        StoreToDatabase.storeTempPosition(parkingPositionObj , writableDatabase);
        closeWritableDatabase(writableDatabase);
    }

    /**
     * method to get location changes passed from requestLocationUpdates.
     */
    private void onLocationChanged(Location location){
        Float distanceFromVehicle = null;
        Log.d(Helper.TAG,"Location Changed To -> lat:"+ location.getLatitude()+ " lon:"+ location.getLongitude() +" acc:"+ location.getAccuracy());
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        if(vehicleParked){
            if( ! (distance_text1.getVisibility()== View.VISIBLE  && distance_text2.getVisibility() == View.VISIBLE) ){
                //TODO need UI change
                distance_text1.setVisibility(View.VISIBLE);
                distance_text2.setVisibility(View.VISIBLE);
            }
            distanceFromVehicle = location.distanceTo(parkedLocation);
            distance_text2.setText(String.valueOf(distanceFromVehicle));
            Log.d(Helper.TAG , "Distance from your vehicle -> "+distanceFromVehicle);
        }
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
        String [] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permission, REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void releaseParkingLocation(boolean release) {
        if(release){
            DeleteFromDatabase.deleteTempPosition(getBaseContext());
            vehicleParked = false;
        }
    }

    private SQLiteDatabase openReadableDatabase(){
        SQLiteDatabase readableDatabase = new ParkingDBHelper(this).getReadableDatabase();
        return  readableDatabase;
    }

    private void closeReadableDatabase(SQLiteDatabase readableDatabase){
        readableDatabase.close();
    }

    private SQLiteDatabase openWritableDatabase(){
        SQLiteDatabase writableDatabase = new ParkingDBHelper(this).getWritableDatabase();
        return writableDatabase;
    }

    private void closeWritableDatabase(SQLiteDatabase writableDatabase){
        writableDatabase.close();
    }
}
