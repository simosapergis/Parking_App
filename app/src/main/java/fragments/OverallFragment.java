package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sapergis.parking.ParkingStatisticsActivity;
import com.sapergis.parking.R;

import java.util.List;

import adapters.OverallRecyclerViewAdapter;
import interfaces.ParkingEntriesInterface;
import objects.ParkingPositionObject;


public class OverallFragment extends Fragment{
    List<ParkingPositionObject> parkingEntriesList ;
    private RecyclerView recyclerView ;
    Activity activity;
    View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.listview_fragment_layout , container , false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
            super.onResume();
            initiateListComponents();
    }


    private void initiateListComponents() {
        activity =((ParkingStatisticsActivity) getActivity());
        ParkingEntriesInterface getList =(ParkingEntriesInterface) activity;
        try {
            parkingEntriesList = getList.parkingEntriesList();
        }catch (NullPointerException nex){
            nex.printStackTrace();
        }
        OverallRecyclerViewAdapter adapter;
        adapter = new OverallRecyclerViewAdapter(parkingEntriesList);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
