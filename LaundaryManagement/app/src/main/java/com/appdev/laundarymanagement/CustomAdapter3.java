package com.appdev.laundarymanagement;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter3 extends ArrayAdapter<pricelistclass>{
    private static final String TAG = "PersonListAdapter";
    private final Context mContext;
    private final int mResource;
    EditText etcurr;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<pricelistclass> mobjects;
    private int lastPosition = -1;
    private List<EditText> editTextList;
    private static class ViewHolder {
        TextView particular;
        TextView unit;
        TextView rate;
        EditText quantity;
        TextView totalprice;

    }
    public CustomAdapter3(Context context, int resource, ArrayList<pricelistclass> objects,int Size) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mobjects=objects;
        this.editTextList = new ArrayList<>();
        for(int i=0;i<Size;i++) {
            data.add("0");
        }

        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String particular = getItem(position).getParticular();
        String unit = getItem(position).getUnit();
        String rate= getItem(position).getRate();
        String quantity=getItem(position).getQuantity();
        String Totalprice=getItem(position).getTotalprice();
        System.out.println(quantity);
        //Create the person object with the information
        pricelistclass priceList = new pricelistclass(particular,unit,rate,Totalprice);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        com.appdev.laundarymanagement.CustomAdapter3.ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.particular = (TextView) convertView.findViewById(R.id.textView4);
            holder.unit = (TextView) convertView.findViewById(R.id.textView5);
            holder.rate = (TextView) convertView.findViewById(R.id.textView6);
            holder.quantity=(EditText)convertView.findViewById(R.id.edittext);
            holder.totalprice=(TextView)convertView.findViewById(R.id.totaltv);
            getItem(position).setQuantity(holder.quantity.getText().toString());
            editTextList.add(holder.quantity);
            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (com.appdev.laundarymanagement.CustomAdapter3.ViewHolder) convertView.getTag();
            result = convertView;
        }
        lastPosition = position;
        holder.particular.setText(priceList.getParticular());
        holder.unit.setText(priceList.getUnit());
        holder.rate.setText(priceList.getRate());
        holder.totalprice.setText(priceList.getTotalprice());
        holder.quantity.setText(data.get(position));
        holder.quantity.setId(position);
        holder.quantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    data.set(position, Caption.getText().toString());
                    etcurr = (EditText) v;
                    holder.totalprice.setText(priceList.getTotalprice());
                }
                else{
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
                    data.set(position, Caption.getText().toString());
                    etcurr = (EditText) v;
                }
            }
        });

        return convertView;

    }
}
