package com.sapergis.parking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import java.util.List;


import adapters.MyPagerAdapter;
import database.ParkingDBHelper;
import helperClasses.Helper;
import interfaces.ParkingEntriesInterface;
import objects.ParkingPositionObject;

public class ParkingStatisticsActivity extends ParentActivity implements ParkingEntriesInterface {

    List<ParkingPositionObject> parkingEntriesList;
    List<String> parkingDistinctValuesList;
    double [] areasPercentages;
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
        ParkingDBHelper parkingDBHelper = ParkingDBHelper.getParkingDBHelperInstance(this);
        parkingEntriesList = parkingDBHelper.retrieveAllParkingEntries(current_username, Helper.TEST_DATA_USERNAME);
        parkingDistinctValuesList = parkingDBHelper.retrieveStatistics(current_username, Helper.TEST_DATA_USERNAME);
        calculateStatistics(parkingEntriesList,  parkingDistinctValuesList);
    }

    private void calculateStatistics(List<ParkingPositionObject> ppoList, List <String> pdvList){
        final int pdvSize = pdvList.size();
        final int ppoSize = ppoList.size();
        final int percentage = 100;
        int [] areaCount  = new int[pdvSize];
        areasPercentages = new double[pdvSize];
        for(int i=0; i<pdvList.size(); i++) {
                for (int j = 0; j < ppoList.size(); j++) {
                    if( pdvList.get(i).equalsIgnoreCase(ppoList.get(j).getArea()) ){
                            areaCount[i]+=1;
                        }
                    }
        }
        for(int i=0; i<areaCount.length; i++){
            areasPercentages[i] = (percentage * areaCount[i]) / ppoSize;
        }

    }
    
    @Override
    public List<ParkingPositionObject> parkingEntriesList() {
        return parkingEntriesList;
    }

    @Override
    public List<String> areasVisited() {
        return parkingDistinctValuesList;
    }

    @Override
    public double[] areaPercentages() {
        return areasPercentages;
    }


}
