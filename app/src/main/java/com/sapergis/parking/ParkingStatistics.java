package com.sapergis.parking;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapters.RecyclerViewAdapter;
import database.ParkingDBHelper;
import database.RetrieveFromDatabase;
import helperClasses.Helper;
import objects.ParkingPositionObject;

public class ParkingStatistics extends AppCompatActivity{
    List<ParkingPositionObject> parkingEntriesList ;
    private String current_username;
    private RecyclerView recyclerView ;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_statistics);
        Intent intent = getIntent();
        current_username = intent.getStringExtra(Helper.CURRENT_USERNAME);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadParkingEntriesList();
        initiateListComponents();
    }

    private void loadParkingEntriesList() {
        SQLiteDatabase readableDatabase = new ParkingDBHelper(this).getWritableDatabase();
        parkingEntriesList = RetrieveFromDatabase.retrieveAllEntries(readableDatabase , current_username);
        for(ParkingPositionObject parkingPositionObject : parkingEntriesList){
            System.out.println(parkingPositionObject.toString());
        }
        readableDatabase.close();
    }

    private void initiateListComponents() {
        adapter = new RecyclerViewAdapter(parkingEntriesList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
