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
        ImageView severityIcon = (ImageView) rowView.findViewById(R.id.severity_icon);
//        textView.setText(values[position]);
        reportTime.setText(values.get(position).get("time"));
        reportLocation.setText(values.get(position).get("location"));
        reportTitle.setText(values.get(position).get("title"));
        final String desc = values.get(position).get("description");
        final int severity = Integer.parseInt(values.get(position).get("severity"));
        // Change the icon for Windows and iPhone
        switch(severity){
            case 0:
                severityIcon.setImageResource(R.drawable.zeroseverity);
                break;
            case 1:
                severityIcon.setImageResource(R.drawable.oneseverity);
                break;
            case 2:
                severityIcon.setImageResource(R.drawable.twoseverity);
                break;
            case 3:
                severityIcon.setImageResource(R.drawable.threeseverity);
                break;
            case 4:
                severityIcon.setImageResource(R.drawable.fourseverity);
                break;
        }

        final String reportid = "";
        final Context currentContext = this.context;
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0) {
                Intent intent = new Intent(context, ViewIncidentActivity.class);
                intent.putExtra("reportTime", reportTime.getText().toString());
                intent.putExtra("reportTitle", reportTitle.getText().toString());
                intent.putExtra("reportLocation", reportLocation.getText().toString());
                intent.putExtra("severity", severity);
                intent.putExtra("reportDesc", desc);
                context.startActivity(intent);
            }
        });
        return rowView;
    }
}
