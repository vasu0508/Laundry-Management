package com.appdev.laundarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter6 extends ArrayAdapter<history_bifurcation_class>{
    private static final String TAG = "PersonListAdapter";
    private final Context mContext;
    private final int mResource;
    ArrayList<history_bifurcation_class> mobjects;
    private int lastPosition = -1;
    private static class ViewHolder {
        TextView particular;
        TextView quantity;
    }
    public CustomAdapter6(Context context, int resource, ArrayList<history_bifurcation_class> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mobjects=objects;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String particular = getItem(position).getParticular();
        String quantity = getItem(position).getQuantity();

        //Create the person object with the information
        history_bifurcation_class priceList = new history_bifurcation_class(particular,quantity);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        com.appdev.laundarymanagement.CustomAdapter6.ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.particular = (TextView) convertView.findViewById(R.id.textView4);
            holder.quantity = (TextView) convertView.findViewById(R.id.textView6);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (com.appdev.laundarymanagement.CustomAdapter6.ViewHolder) convertView.getTag();
            result = convertView;
        }
        lastPosition = position;

        holder.particular.setText(priceList.getParticular());
        holder.quantity.setText(priceList.getQuantity());

        return convertView;
    }
}
