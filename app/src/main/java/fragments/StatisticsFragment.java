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
import adapters.StatisticsRecyclerViewAdapter;
import interfaces.ParkingEntriesInterface;
import objects.ParkingPositionObject;


public class StatisticsFragment extends Fragment {
    List<String> areasVisited ;
    double [] areaStatistics;
    private RecyclerView recyclerView ;
    Activity activity;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.statistics_fragment_layout, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initiateListComponents();
    }

    private void initiateListComponents() {
        activity =((ParkingStatisticsActivity) getActivity());
        ParkingEntriesInterface getData =(ParkingEntriesInterface) activity;
        try {
            areasVisited = getData.areasVisited();
            areaStatistics = getData.areaPercentages();
        }catch (NullPointerException nex){
            nex.printStackTrace();
        }
        StatisticsRecyclerViewAdapter adapter;
        adapter = new StatisticsRecyclerViewAdapter(areasVisited , areaStatistics);
        recyclerView.addItemDecoration(new DividerItemDecoration(v.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
