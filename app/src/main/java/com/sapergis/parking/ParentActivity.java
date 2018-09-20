package com.sapergis.parking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Switch;

import com.google.android.gms.internal.ge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import helperClasses.Helper;


public class ParentActivity extends AppCompatActivity {
    protected SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor =null;

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }

    protected void changeLocale(String localeValue){
        Configuration config  = getResources().getConfiguration();
        Locale locale = new Locale(localeValue);
        if(Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Resources resources = new Resources(getAssets(),metrics,config);
            Locale.setDefault(locale);
            refreshActivityTitle();
            recreate();
        }
    }

    protected synchronized SharedPreferences getAppSharedPreferences (){
        if(sharedPreferences == null){
            sharedPreferences = getSharedPreferences(Helper.PREF_NAME, 0);
        }
        return sharedPreferences;
    }

    protected void updateSharedPreferences(HashMap<String,Object> hashMap){
        editor = getAppSharedPreferences().edit();
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            String dataType = pair.getValue().getClass().getSimpleName();
            switch (dataType){
                case Helper.STRING:
                    editor.putString(pair.getKey().toString() , pair.getValue().toString());
                    break;
                case Helper.INT:
                    editor.putInt(pair.getKey().toString() , (int)pair.getValue());
                    break;
                case Helper.FLOAT:
                    editor.putFloat(pair.getKey().toString() , (float)pair.getValue());
                    break;
                case Helper.BOOLEAN:
                    editor.putBoolean(pair.getKey().toString() , (boolean)pair.getValue());
                    break;
                }
        }
        editor.apply();

    }



    private void refreshActivityTitle(){
        setTitle(getResources().getString(R.string.app_name));
    }
}
