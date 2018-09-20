package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sapergis.parking.R;

import java.util.List;

import helperClasses.Helper;

public class LocaleArrayAdapter extends ArrayAdapter {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<String> localeList;
    private final List<Integer> flagsList;
    private final int resource;

    public LocaleArrayAdapter(@NonNull Context context, int resource, @NonNull List objects, List objects2) {
        super(context, resource, objects);
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.resource = resource;
        this.localeList = objects;
        this.flagsList = objects2;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    private View createItemView (int position, View convertView, ViewGroup parent){
        final View view = layoutInflater.inflate(resource, parent, false);
        ImageView flag = (ImageView)view.findViewById(R.id.flagIcon);
        TextView language = (TextView)view.findViewById(R.id.language);
        flag.setImageResource(flagsList.get(position));
        language.setText(localeList.get(position));
        return view;
    }

}
