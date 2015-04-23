package com.vigilanteosu.cse4471.vigilanteosuapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/*
 * Created by alpal on 11/30/14.
 */
public class FeedArrayAdapter extends ArrayAdapter<HashMap<String, String>> {
    private final Context context;
    private ArrayList<HashMap<String, String>> values;

    public FeedArrayAdapter(Context context, HashMap<String, String>[] value) {
        super(context, R.layout.activity_feed_row, value);
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
        View rowView = inflater.inflate(R.layout.activity_feed_row, parent, false);
        final TextView reportTime = (TextView) rowView.findViewById(R.id.report_time);
        final TextView reportTitle = (TextView) rowView.findViewById(R.id.report_title);
        final TextView reportLocation = (TextView) rowView.findViewById(R.id.report_location);
        final TextView reportDesc = (TextView) rowView.findViewById(R.id.report_desc);

        String subject = values.get(position).get("title");
        if(subject.contains("Burglary")){
            subject = "Burglary";
        }else{
            int pos = subject.lastIndexOf('-');
            if(pos > 0){
                subject = subject.substring(0, pos);
            }
        }

        reportTime.setText(values.get(position).get("time"));
        reportLocation.setText(values.get(position).get("location"));
        reportTitle.setText(subject);
        reportDesc.setText(values.get(position).get("description"));

        final String reportid = values.get(position).get("reportid");

        final String lon = values.get(position).get("lon");
        final String lat = values.get(position).get("lat");


        final Context currentContext = this.context;
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0) {
                Intent intent = new Intent(context, ViewIncidentActivity.class);
                intent.putExtra("reportTime", reportTime.getText().toString());
                intent.putExtra("reportTitle", reportTitle.getText().toString());
                intent.putExtra("reportLocation", reportLocation.getText().toString());
                intent.putExtra("reportDesc", reportDesc.getText().toString());
                intent.putExtra("reportid", reportid);

                if(!lon.equals("None") && !lat.equals("None")) {
                    intent.putExtra("lon", lon);
                    intent.putExtra("lat", lat);
                }else{
                    intent.putExtra("lon", "nah");
                    intent.putExtra("lat", "nah");
                }

                context.startActivity(intent);
            }
        });
        return rowView;
    }
}
