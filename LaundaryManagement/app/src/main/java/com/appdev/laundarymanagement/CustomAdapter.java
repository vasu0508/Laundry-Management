package com.appdev.laundarymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<newClass> {
    private static final String TAG = "PersonListAdapter";
    private final Context mContext;
    private final int mResource;
    ArrayList<newClass> mobjects;
    private int lastPosition = -1;
    private static class ViewHolder {
        TextView title;
        TextView Value;
    }
    public CustomAdapter(Context context, int resource, ArrayList<newClass> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mobjects=objects;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).gettitle();
        String Value = getItem(position).getValue();
        String CardNo=getItem(position).getCardNo();

        //Create the person object with the information
        newClass Newclass = new newClass(title,Value,CardNo);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.textView);
            holder.Value = (TextView) convertView.findViewById(R.id.textView2);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        lastPosition = position;

        holder.title.setText(Newclass.gettitle());
        holder.Value.setText(Newclass.getValue());

        return convertView;
    }
}
