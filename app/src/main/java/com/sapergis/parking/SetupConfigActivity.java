package com.sapergis.parking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Locale;

import adapters.LocaleArrayAdapter;
import adapters.VehicleArrayAdapter;
import helperClasses.Helper;

public class SetupConfigActivity extends ParentActivity {

    private EditText username;
    private Spinner vehicle_spinner;
    private Spinner locale_spinner;
    private CheckBox storeCheckBox;
    private CheckBox logsCheckBox;
    private VehicleArrayAdapter vhclAdapter;
    private LocaleArrayAdapter localeArrayAdapter;
   // private ImageView grLocale;
    private Button doneBtn;
    boolean selected = false;
    LayoutInflater mInflator;
    ArrayList<String> vehicleList;
    ArrayList<String> languages;
    ArrayList<Integer> flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_config);
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(loadedAgain()){
            retrieveSharedPreferences();
        }
        else {
            showWelcomeMessage();
            vhclAdapter.insert(getHeaderValue(), 0);
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
        vehicle_spinner = (Spinner)findViewById(R.id.vehicleSpinner);
        locale_spinner = (Spinner)findViewById(R.id.localeSpinner);
        vehicleList  = getVehicleσList();
        languages = getAvailableLanguages();
        flags = getAvailableFlags();
        vhclAdapter = new VehicleArrayAdapter(this, R.layout.spinner_layout, vehicleList);
        localeArrayAdapter = new LocaleArrayAdapter(this, R.layout.locale_spinner_layout, languages, flags);
        vehicle_spinner.setAdapter(vhclAdapter);
        vehicle_spinner.setOnItemSelectedListener(onItemSelectedListener);
        vehicle_spinner.setOnTouchListener(onTouchListener);
        locale_spinner.setAdapter(localeArrayAdapter);

//        grLocale = (ImageView) findViewById(R.id.gr);
//        grLocale.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(Helper.TAG, " Is Connected ? "+ isNetworkAvailable());
//            }
//        });
        //if(Locale.)
        /*
        To delete below
         */
//        localeButton  = (Button) findViewById(R.id.localeButton);
//
//        localeButton.setOnClickListener(new View.OnClickListener() {
//            /*
//                needs to be changed
//            */
//            @Override
//            public void onClick(View view) {
//                changeLocale("en_US");
//            }
//        });

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
            String header = getHeaderValue();
            if(vhclAdapter.getItem(0).equals(header)){
                vhclAdapter.remove(header);
                vhclAdapter.notifyDataSetChanged();
            }
            return false;   
        }
    };
    //Below to be moved in Parent Activity  -  updatedSharedPreferences()
    private void storeSharedPreferences(){
        if(username.getText().toString().isEmpty() ){
            Toast.makeText(getBaseContext(), R.string.please_enter_username ,Toast.LENGTH_LONG).show();
        }
        else if(vehicle_spinner.getSelectedItem().equals(getHeaderValue())){
            Toast.makeText(getBaseContext(), R.string.please_select_vehicle ,Toast.LENGTH_LONG).show();
        }
        else{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(!getAppSharedPreferences().contains(Helper.PREF_EXISTS)){
                hashMap.put(Helper.PREF_EXISTS , true);
            }
            hashMap.put(Helper.PREF_USERNAME, username.getText().toString());
            hashMap.put(Helper.PREF_VEHICLE , vehicle_spinner.getSelectedItem().toString());
            hashMap.put(Helper.PREF_LASTSELECTEDLOCALE, Locale.getDefault().toString());
            hashMap.put(Helper.PREF_VEHICLE_ROW , vehicle_spinner.getSelectedItemPosition());
            hashMap.put(Helper.PREF_ALLOWPARKINGENTRIES , storeCheckBox.isChecked());
            hashMap.put(Helper.PREF_ALLOWCONSOLELOGS , storeCheckBox.isChecked());
            updateSharedPreferences(hashMap);
//            SharedPreferences.Editor editor = getAppSharedPreferences().edit();
//            if(!getAppSharedPreferences().contains(Helper.PREF_EXISTS)){
//                editor.putBoolean(Helper.PREF_EXISTS , true);
//            }
//            editor.putString(Helper.PREF_USERNAME , username.getText().toString());
//            editor.putString(Helper.PREF_VEHICLE , vehicle_spinner.getSelectedItem().toString());
//            editor.putString(Helper.PREF_LASTSELECTEDLOCALE, Locale.getDefault().toString());
//            editor.putInt(Helper.PREF_VEHICLE_ROW , vehicle_spinner.getSelectedItemPosition());
//            editor.putBoolean(Helper.PREF_ALLOWPARKINGENTRIES , storeCheckBox.isChecked());
//            editor.putBoolean(Helper.PREF_ALLOWCONSOLELOGS , logsCheckBox.isChecked());
//            editor.apply();
//            Helper.logsEnabled = logsCheckBox.isChecked();
//            if(Helper.logsEnabled){
//                Log.d(Helper.TAG , "Username = "+getAppSharedPreferences().getString("username","isNuLL"));
//                Log.d(Helper.TAG , "Vehile = "+getAppSharedPreferences().getString("vehicle","isNuLL"));
//                Log.d(Helper.TAG , "isChecked = "+getAppSharedPreferences().getBoolean("allowParkingEntries", false));
//                Log.d(Helper.TAG , "isChecked = "+getAppSharedPreferences().getBoolean("allowConsoleLogs", true));
//            }

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void retrieveSharedPreferences(){
        username.setText(getAppSharedPreferences().getString(Helper.PREF_USERNAME,null));
        vehicle_spinner.setSelection(getAppSharedPreferences().getInt(Helper.PREF_VEHICLE_ROW, 0));
        storeCheckBox.setChecked(getAppSharedPreferences().getBoolean(Helper.PREF_ALLOWPARKINGENTRIES, true));
        logsCheckBox.setChecked(getAppSharedPreferences().getBoolean(Helper.PREF_ALLOWCONSOLELOGS, true));
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private ArrayList<String> getVehicleσList(){
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list,getResources().getStringArray(R.array.vehicles_list));
        return list;
    }

    private ArrayList<String> getAvailableLanguages(){
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list,getResources().getStringArray(R.array.languages_list));
        return list;
    }

    private ArrayList<Integer> getAvailableFlags(){
        ArrayList<Integer> flagsList = new ArrayList<Integer>();
        flagsList.add(0,R.mipmap.ic_uk_flag);
        flagsList.add(1,(R.mipmap.ic_greek_flag));
        return flagsList;
    }

    private boolean loadedAgain(){
        return  getAppSharedPreferences().getBoolean(Helper.PREF_EXISTS, false);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    private String getHeaderValue(){

        return getResources().getString(R.string.select_vehicle);
    }

}
