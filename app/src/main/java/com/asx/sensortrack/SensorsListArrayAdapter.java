package com.asx.sensortrack;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class SensorsListArrayAdapter extends ArrayAdapter<SensorEntry> {
    List<SensorEntry> mSensorsEntries;
    int mItemResource;
    Context mContext;

    public SensorsListArrayAdapter(Context context, int resource, List<SensorEntry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mSensorsEntries = objects;
        this.mItemResource = resource;
    }

    @Override
    public int getCount() {
        return mSensorsEntries.size();
    }

    @Override
    public SensorEntry getItem(int position) {
        return mSensorsEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SensorItemHolder holder;
        View rootView = convertView;

        if (rootView == null) {
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            holder = new SensorItemHolder();
            rootView = inflater.inflate(mItemResource, parent, false);
            holder.name = (TextView)rootView.findViewById(R.id.sensorName);
            holder.plotCB = (CheckBox)rootView.findViewById(R.id.plotCheckBox);
            holder.saveCB = (CheckBox)rootView.findViewById(R.id.saveCheckBox);

            rootView.setTag(holder);
        } else {
            holder = (SensorItemHolder)rootView.getTag();
        }

        SensorEntry item = mSensorsEntries.get(position);

        holder.name.setText(item.getName());

        holder.plotCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSensorsEntries.get(position).setIsPlotting(isChecked);
            }
        });

        holder.saveCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSensorsEntries.get(position).setIsSaving(isChecked);
            }
        });

        return rootView;
    }



    private class SensorItemHolder {
        TextView name;
        CheckBox plotCB;
        CheckBox saveCB;
    }
}
