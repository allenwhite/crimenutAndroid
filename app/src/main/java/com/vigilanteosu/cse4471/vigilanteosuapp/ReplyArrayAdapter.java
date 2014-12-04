package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by neil on 11/30/14.
 */
public class ReplyArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final Context context;
    private ArrayList<HashMap<String, String>> values;

    public ReplyArrayAdapter(Context context, HashMap<String, String>[] value) {
        super(context, R.layout.activity_view_incident_row, value);
        this.context = context;
        this.values = new ArrayList <HashMap<String, String>>();
        for(int i=0;i<value.length;i++){
            if(value[i] != null){
                this.values.add(value[i]);
            }
        }
    }

    @Override
    public void addAll(Collection<? extends HashMap<String, String>> collection){
        this.values.addAll(collection);
    }

    @Override
    public void clear(){
        this.values.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_view_incident_row, parent, false);
        final TextView reportReply = (TextView) rowView.findViewById(R.id.report_reply);
        reportReply.setText(values.get(position).get("body"));


        //???????final Context currentContext = this.context;

        return rowView;
    }
}
