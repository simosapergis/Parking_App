package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sapergis.parking.R;

import java.util.Date;
import java.util.List;

import objects.ParkingPositionObject;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private List <ParkingPositionObject> ppList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView date, address, area , time;
        public MyViewHolder(View view) {
            super(view);
            date =(TextView)view.findViewById(R.id.text_date);
            address =(TextView)view.findViewById(R.id.text_address);
            area =(TextView)view.findViewById(R.id.text_area);
            //Todo time <--
        }
    }
    public RecyclerViewAdapter(List<ParkingPositionObject> list){
        this.ppList = list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterview_row_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ParkingPositionObject ppo = ppList.get(position);
        View addressView = holder.address;
        String addressText = addressView.getResources().getString(R.string.parked_at);
        String vehicle = ppo.getVehicle();
        long dt = ppo.getDatetime();
        Date date = new Date(dt);
        holder.area.setText(ppo.getArea());
        holder.address.setText(vehicle+addressText+ppo.getArea());
        holder.date.setText(date.toString());
        //TODO holder.time.setText("time");
    }

    @Override
    public int getItemCount() {
        return ppList.size();
    }
}
