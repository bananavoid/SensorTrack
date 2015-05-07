package com.asx.sensortrack;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;


public class SelectedSensorsCursorAdapter extends CursorAdapter {


    public SelectedSensorsCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.sensors_list_rate_entry, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SensorItemHolder holder;
        View rootView = view;

        holder = new SensorItemHolder();
        holder.name = (TextView)rootView.findViewById(R.id.sensorName);
        holder.rateEdit = (EditText)rootView.findViewById(R.id.rateEdit);

        holder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

    }

    private class SensorItemHolder {
        TextView name;
        EditText rateEdit;
    }
}
