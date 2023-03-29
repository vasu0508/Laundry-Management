package com.appdev.laundarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter5 extends ArrayAdapter<UserHistoryClass> {
    private final Context mContext;
    private final int mResource;
    ArrayList<UserHistoryClass> mobjects;
    private int lastPosition = -1;
    private static class ViewHolder {
        TextView status;
        TextView amount;
        TextView quantity;
        TextView date;
        TextView time;
    }
    public CustomAdapter5(Context context, int resource, ArrayList<UserHistoryClass> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mobjects=objects;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String status = getItem(position).getStatus();
        String amount = getItem(position).getAmount();
        String quantity = getItem(position).getQuantity();
        String date=getItem(position).getDate();
        String time=getItem(position).getTime();

        //Create the person object with the information
        UserHistoryClass rclass = new UserHistoryClass(status,amount,quantity,date,time);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.amount = (TextView) convertView.findViewById(R.id.textView4);
            holder.quantity = (TextView) convertView.findViewById(R.id.textView3);
            holder.status = (TextView) convertView.findViewById(R.id.textView2);
            holder.date=(TextView)convertView.findViewById(R.id.textView7);
            holder.time=(TextView)convertView.findViewById(R.id.textView8);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        lastPosition = position;

        holder.status.setText(rclass.getStatus());
        holder.amount.setText(rclass.getAmount());
        holder.quantity.setText(rclass.getQuantity());
        holder.date.setText(rclass.getDate());
        holder.time.setText(rclass.getTime());

        return convertView;
    }
}
