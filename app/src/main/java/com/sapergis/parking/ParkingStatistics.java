package com.sapergis.parking;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import database.ParkingDBHelper;
import database.RetrieveFromDatabase;
import helperClasses.Helper;
import objects.ParkingPositionObject;

public class ParkingStatistics extends AppCompatActivity {
    private String current_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_statistics);
        Intent intent = getIntent();
        current_username = intent.getStringExtra(Helper.CURRENT_USERNAME);

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ParkingPositionObject> parkingEntries ;
        SQLiteDatabase readableDatabase = new ParkingDBHelper(this).getWritableDatabase();
        parkingEntries = RetrieveFromDatabase.retrieveAllEntries(readableDatabase , current_username);
        for(ParkingPositionObject parkingPositionObject : parkingEntries){
            System.out.println(parkingPositionObject.toString());
        }
        readableDatabase.close();
    }
}
