package com.sapergis.parking;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import helperClasses.Helper;

public class SetupConfigActivity extends AppCompatActivity {


    private TextInputEditText username;
    private Spinner vehicle_spinner;
    private CheckBox storeCheckBox;
    private Button doneBtn;
    boolean selected = false;
    LayoutInflater mInflator;
    SharedPreferences sharedPreferences;
    private String [] data;

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
                        storeSharedPreferences();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupViews(){
        mInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        data = getResources().getStringArray(R.array.vehicles_list);
        username = findViewById(R.id.username);
        storeCheckBox = findViewById(R.id.checkBox);
        doneBtn = (Button)findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(onClickListener);
        vehicle_spinner = (Spinner) findViewById(R.id.vehicleSpinner);
        vehicle_spinner.setAdapter(spinnerAdapter);
        vehicle_spinner.setOnItemSelectedListener(onItemSelectedListener);
        vehicle_spinner.setOnTouchListener(onTouchListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           storeSharedPreferences();
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
            editor.apply();
            Log.d(Helper.TAG , "Username = "+sharedPreferences.getString("username","isNuLL"));
            Log.d(Helper.TAG , "Vehile = "+sharedPreferences.getString("vehicle","isNuLL"));
            Log.d(Helper.TAG , "isChecked = "+sharedPreferences.getBoolean("allowParkingEntries", false));
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
    }
    private SpinnerAdapter spinnerAdapter = new BaseAdapter() {
            private  TextView text;
            private int count = 4;


            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
               if(view == null){
                   view = mInflator.inflate(R.layout.spinner_layout, null);
                   text = (TextView)view.findViewById(R.id.spinnerTarget);
                   if(!sharedPreferences.getBoolean(Helper.PREF_EXISTS,false ) && !selected){
                       text.setText(data[count]);
                   }else{
                       text.setText(data[position]);
                   }
               }
               return view;
            }

            @Override
            public int getCount() {
                return count-1;
            }

            @Override
            public Object getItem(int position) {
                return data[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

        };


        private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Log.d(HelperClass.TAG , "selected : "+ vehicle_spinner.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(Helper.TAG , "Nothing selected");
            }
        };

        private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                selected = true;
                ((BaseAdapter)spinnerAdapter).notifyDataSetChanged();
                return false;
            }
        };

}
