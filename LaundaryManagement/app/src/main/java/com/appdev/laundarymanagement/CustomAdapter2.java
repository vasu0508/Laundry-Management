package com.appdev.laundarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter2 extends ArrayAdapter<pricelistclass>{
        private static final String TAG = "PersonListAdapter";
        private final Context mContext;
        private final int mResource;
        ArrayList<pricelistclass> mobjects;
        private int lastPosition = -1;
        private static class ViewHolder {
            TextView particular;
            TextView unit;
            TextView rate;
        }
        public CustomAdapter2(Context context, int resource, ArrayList<pricelistclass> objects) {
            super(context, resource, objects);
            mContext = context;
            mResource = resource;
            mobjects=objects;

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String particular = getItem(position).getParticular();
            String unit = getItem(position).getUnit();
            String rate= getItem(position).getRate();

            //Create the person object with the information
            pricelistclass priceList = new pricelistclass(particular,unit,rate);

            //create the view result for showing the animation
            final View result;

            //ViewHolder object
            com.appdev.laundarymanagement.CustomAdapter2.ViewHolder holder;


            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHolder();
                holder.particular = (TextView) convertView.findViewById(R.id.textView4);
                holder.unit = (TextView) convertView.findViewById(R.id.textView5);
                holder.rate = (TextView) convertView.findViewById(R.id.textView6);
                result = convertView;

                convertView.setTag(holder);
            }
            else{
                holder = (com.appdev.laundarymanagement.CustomAdapter2.ViewHolder) convertView.getTag();
                result = convertView;
            }
            lastPosition = position;

            holder.particular.setText(priceList.getParticular());
            holder.unit.setText(priceList.getUnit());
            holder.rate.setText(priceList.getRate());

            return convertView;
        }
}
