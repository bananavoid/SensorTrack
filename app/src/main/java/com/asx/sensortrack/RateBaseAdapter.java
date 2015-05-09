package com.asx.sensortrack;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.asx.sensortrack.database.DbUtils;

import java.util.HashMap;
import java.util.List;


public class RateBaseAdapter extends BaseAdapter {
    List<SensorEntry> mEntries;
    Context mContext;
    HashMap<Long, Integer> mTextValues =new HashMap<>();
    LayoutInflater mInflater;

    public RateBaseAdapter(Context context, List<SensorEntry> entries) {
        this.mEntries = entries;
        this.mContext = context;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);

        for (SensorEntry entry : mEntries){
            mTextValues.put(entry.getId(), entry.getRate());
        }
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public SensorEntry getItem(int position) {
        return mEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEntries.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SensorItemHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.sensors_list_rate_entry, null);
            holder = new SensorItemHolder();
            convertView.setTag(holder);
        } else {
            holder = (SensorItemHolder) convertView.getTag();
        }

        holder.name = (TextView)convertView.findViewById(R.id.sensorName);
        holder.rateEdit = (EditText)convertView.findViewById(R.id.rateEdit);
        holder.ref = getItemId(position);

        holder.rateEdit.setText(String.valueOf(mTextValues.get(getItemId(position))));
        holder.name.setText(getItem(position).getName());

        holder.rateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = mTextValues.get(holder.ref).toString();
                if (!s.toString().equals("") && !s.toString().equals(temp)) {
                    mTextValues.put(holder.ref, Integer.valueOf(s.toString()));
                }
            }
        });

        return convertView;
    }

    private class SensorItemHolder {
        TextView name;
        EditText rateEdit;
        long ref;
    }
}
