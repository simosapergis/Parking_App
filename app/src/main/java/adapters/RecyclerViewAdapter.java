package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sapergis.parking.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import helperClasses.Helper;
import objects.ParkingPositionObject;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private List <ParkingPositionObject> ppList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView date, address, area , time;
        public MyViewHolder(View view) {
            super(view);
            date =(TextView)view.findViewById(R.id.text_date);
            time = (TextView)view.findViewById(R.id.text_time);
            address =(TextView)view.findViewById(R.id.text_address);
            area =(TextView)view.findViewById(R.id.text_area);
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
        String vehicle = ppo.getVehicle();
        String addressText = formatAddressText(ppo , holder);
        String [] dateTime = formatDateTime(ppo);
        holder.date.setText(dateTime[0]);
        holder.time.setText(dateTime[1]);
        holder.area.setText(ppo.getArea());
        holder.address.setText(addressText);

      //  holder.
        //TODO holder.time.setText("time");
    }

    private String formatAddressText(ParkingPositionObject ppo , MyViewHolder holder){
        View addressView = holder.address;
        StringBuilder sb = new StringBuilder();
        String addressText = addressView.getResources().getString(R.string.parked_at);
        sb.append(ppo.getVehicle()).append(" ").append(addressText).append(" ").append(ppo.getParked_address()).append(" ").append(ppo.getParked_address_no());
        return sb.toString();
    }

    private String[] formatDateTime(ParkingPositionObject ppo){
        String [] dateTime = new String[2];
        long dt = ppo.getDatetime();
        DateFormat dateFormat = new SimpleDateFormat(Helper.DATE_PATTERN);
        DateFormat timeFormat = new SimpleDateFormat(Helper.TIME_PATTERN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dt);
        Date date = new Date(dt);
        dateTime[0] = dateFormat.format(date);
        dateTime[1] = timeFormat.format(date);
        return dateTime;
    }

    @Override
    public int getItemCount() {
        return ppList.size();
    }
}
