package com.sapergis.parking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import adapters.VehicleArrayAdapter;
import helperClasses.Helper;

public class SetupConfigActivity extends AppCompatActivity {

    private EditText username;
    private Spinner vehicle_spinner;
    private CheckBox storeCheckBox;
    private CheckBox logsCheckBox;
    private VehicleArrayAdapter vhclAdapter;
    private Button localeButton;
    private Button doneBtn;
    boolean selected = false;
    LayoutInflater mInflator;
    SharedPreferences sharedPreferences;
    ArrayList<String> vehicleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_config);
        sharedPreferences = getSharedPreferences(Helper.PREF_NAME , 0);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPreferences.getBoolean(Helper.PREF_EXISTS,false)){
            retrieveSharedPreferences();
        }
        else {
            showWelcomeMessage();
        }

    }

    private void showWelcomeMessage (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.welcome_text)
                .setTitle(R.string.app_name)
                .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupViews(){
        mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        username = findViewById(R.id.username);
        username.setOnEditorActionListener(onEditorActionListener);
        storeCheckBox = findViewById(R.id.checkBox);
        logsCheckBox = findViewById(R.id.logsCheckBox);
        doneBtn = (Button)findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(onClickListener);
        vehicle_spinner = (Spinner) findViewById(R.id.vehicleSpinner);
        vehicleList  = getVehicleList();
        vhclAdapter = new VehicleArrayAdapter(this, R.layout.spinner_layout, vehicleList);
        vehicle_spinner.setAdapter(vhclAdapter);
        vehicle_spinner.setOnItemSelectedListener(onItemSelectedListener);
        vehicle_spinner.setOnTouchListener(onTouchListener);
        /*
        To delete below
         */
        localeButton  = (Button) findViewById(R.id.localeButton);
        localeButton.setOnClickListener(new View.OnClickListener() {
            /*
                needs to be changed
            */
            @Override
            public void onClick(View view) {
                changeLocale("en_US");
            }
        });
        /*

         */

    }

    private EditText.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int keyAction, KeyEvent keyEvent) {
            if(keyAction == EditorInfo.IME_ACTION_DONE){
                return false;
            }
            return true;
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           storeSharedPreferences();
        }
    };
    
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            String header = getResources().getString(R.string.select_vehicle);
            if(vhclAdapter.getItem(0).equals(header)){
                vhclAdapter.remove(header);
                vhclAdapter.notifyDataSetChanged();
            }
            return false;   
        }
    };

    private void storeSharedPreferences(){
        if(!username.getText().toString().isEmpty() ){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!sharedPreferences.contains(Helper.PREF_EXISTS)){
                editor.putBoolean(Helper.PREF_EXISTS , true);
            }
            editor.putString(Helper.PREF_USERNAME , username.getText().toString());
            editor.putString(Helper.PREF_VEHICLE , vehicle_spinner.getSelectedItem().toString());
            editor.putInt(Helper.PREF_VEHICLE_ROW , vehicle_spinner.getSelectedItemPosition());
            editor.putBoolean(Helper.PREF_ALLOWPARKINGENTRIES , storeCheckBox.isChecked());
            editor.putBoolean(Helper.PREF_ALLOWCONSOLELOGS , logsCheckBox.isChecked());
            editor.apply();
            Helper.logsEnabled = logsCheckBox.isChecked();
            if(Helper.logsEnabled){
                Log.d(Helper.TAG , "Username = "+sharedPreferences.getString("username","isNuLL"));
                Log.d(Helper.TAG , "Vehile = "+sharedPreferences.getString("vehicle","isNuLL"));
                Log.d(Helper.TAG , "isChecked = "+sharedPreferences.getBoolean("allowParkingEntries", false));
                Log.d(Helper.TAG , "isChecked = "+sharedPreferences.getBoolean("allowConsoleLogs", true));
            }

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else{
            Toast.makeText(getBaseContext(), R.string.please_enter_username ,Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveSharedPreferences(){
        username.setText(sharedPreferences.getString(Helper.PREF_USERNAME,null));
        vehicle_spinner.setSelection(sharedPreferences.getInt(Helper.PREF_VEHICLE_ROW, 0));
        storeCheckBox.setChecked(sharedPreferences.getBoolean(Helper.PREF_ALLOWPARKINGENTRIES, true));
        logsCheckBox.setChecked(sharedPreferences.getBoolean(Helper.PREF_ALLOWCONSOLELOGS, true));
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            

        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if(Helper.logsEnabled){
                    Log.d(Helper.TAG , "Nothing selected");
                }

            }


        };

    private void changeLocale(String language){
        Configuration config  = getResources().getConfiguration();
        if(Build.VERSION.SDK_INT >= 17) {
                config.setLocale(new Locale(language));
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                Resources resources = new Resources(getAssets(),metrics,config);
                String str = resources.getString(R.string.parked_at);
                System.out.println("Simos ");
                recreate();
            }
        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private ArrayList<String> getVehicleList(){
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list,getResources().getStringArray(R.array.vehicles_list));
        if(loadedAgain()){
            removeHeaderElement(list,getResources().getString(R.string.select_vehicle));
        }
        return list;
    }

    private boolean loadedAgain(){
        return  sharedPreferences.getBoolean(Helper.PREF_EXISTS, false);
    }

    private ArrayList<String> removeHeaderElement (ArrayList<String> vehicleList , String headerValue){
        if (vehicleList.contains(headerValue)){
            vehicleList.remove(headerValue);
        }
        return vehicleList;
    }
    @Override
    public void recreate() {
        super.recreate();
    }
}
