package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import fragments.OverallFragment;
import fragments.StatisticsFragment;
import objects.ParkingPositionObject;


public class MyPagerAdapter extends FragmentStatePagerAdapter {

    int tabsNum=0;
    Context context;
    List<ParkingPositionObject> parkingEntriesList ;

    public MyPagerAdapter(FragmentManager fm , int tabsNum) {
        super(fm);
        this.tabsNum = tabsNum;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
           case 0:
                OverallFragment ofm = new OverallFragment();
                return ofm;
            case 1:
                StatisticsFragment sfm = new StatisticsFragment();
                return sfm;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNum;
    }


}
