package com.asx.sensortrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


public class CustomGridAdapter extends BaseAdapter {
    LayoutInflater inflater;
    String[] mPads = new String[]{};

    public CustomGridAdapter(Context context, String[] entries) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPads = entries;
    }

    @Override
    public final int getCount() {
        return mPads.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.pad_entry, null);
        }

        Button button = (Button) convertView.findViewById(R.id.padButton);
        button.setText(mPads[position]);
        button.setTag(mPads[position]);

        return convertView;
    }
}
