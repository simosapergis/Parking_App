package com.sapergis.parking;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import java.util.List;

import adapters.MyPagerAdapter;
import adapters.RecyclerViewAdapter;
import database.ParkingDBHelper;
import database.RetrieveFromDatabase;
import helperClasses.Helper;
import interfaces.ParkingEntriesInterface;
import objects.ParkingPositionObject;

public class ParkingStatistics extends AppCompatActivity implements ParkingEntriesInterface {

    List<ParkingPositionObject> parkingEntriesList;
    List<String> parkingStatisticsList;
    private String current_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_statistics);
        Intent intent = getIntent();
        current_username = intent.getStringExtra(Helper.CURRENT_USERNAME);
        loadFromDb();
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_overall)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_statistics)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager() , tabLayout.getTabCount());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadFromDb();
        //initiateListComponents();
    }

    private void  loadFromDb() {
        SQLiteDatabase readableDatabase = new ParkingDBHelper(this).getWritableDatabase();
        parkingEntriesList = RetrieveFromDatabase.retrieveAllEntries(readableDatabase , current_username);
        parkingStatisticsList = RetrieveFromDatabase.retrieveStatistics(readableDatabase, current_username);
        readableDatabase.close();
    }
    
    @Override
    public List<ParkingPositionObject> parkingEntriesList() {
        return parkingEntriesList;
    }

    @Override
    public List<ParkingStatistics> parkingStatisticsList() {
        return null;
    }


}
