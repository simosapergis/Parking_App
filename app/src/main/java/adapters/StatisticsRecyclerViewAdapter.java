package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sapergis.parking.R;
import java.util.List;

public class StatisticsRecyclerViewAdapter extends RecyclerView.Adapter<StatisticsRecyclerViewAdapter.MyViewHolder>{
    final String PERCENTAGE_SIGN = "%";
    private List<String> areasVisited;
    private double [] areaStatistics;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView area, percentage;
        public MyViewHolder(View view) {
            super(view);
            area =(TextView)view.findViewById(R.id.area);
            percentage= (TextView)view.findViewById(R.id.percentage);
        }
    }
    public StatisticsRecyclerViewAdapter(List<String> areasVisited, double [] areaStatistics){
        this.areaStatistics = areaStatistics;
        this.areasVisited = areasVisited;
    }


    @Override
    public StatisticsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_row_layout, parent,false);
        return new StatisticsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatisticsRecyclerViewAdapter.MyViewHolder holder, int i) {
        String area = areasVisited.get(i);
        String percentage = String.valueOf(areaStatistics[i]+PERCENTAGE_SIGN);
        holder.area.setText(area);
        holder.percentage.setText(percentage);
    }

    @Override
    public int getItemCount() {
        return areasVisited.size();
    }
}
