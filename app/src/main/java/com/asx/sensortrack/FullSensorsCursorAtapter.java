package com.asx.sensortrack;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class FullSensorsCursorAtapter extends CursorAdapter {
    private static final String TAG = FullSensorsCursorAtapter.class.getSimpleName();
    Context mContext;
    boolean[] mCheckBoxState = new boolean[2];

    public FullSensorsCursorAtapter(Context context, Cursor c) {
        super(context, c, false);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return (int)SensorEntry.count(SensorEntry.class, null, null);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.sensors_list_entry, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.sensors_list_entry,
                    null);
        }
        convertView.setTag(position);
        return super.getView(position, convertView, parent);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getLong(mCursor.getColumnIndex("_id"));
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        SensorItemHolder holder;
        View rootView = view;

        final int position =(Integer) view.getTag();
        final SensorEntry entry = DbUtils.getSensorById(position);

        holder = new SensorItemHolder();
        holder.name = (TextView)rootView.findViewById(R.id.sensorName);
        holder.plotCB = (CheckBox)rootView.findViewById(R.id.plotCheckBox);
        holder.saveCB = (CheckBox)rootView.findViewById(R.id.saveCheckBox);

        holder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

        holder.plotCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        holder.saveCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("DB_UTIL", "Cursor position: " + cursor.getPosition());
                Log.d("DB_UTIL", "position: " + position);

                Log.d("DB_UTIL", "Entry id: " + getItemId(position));

                SensorEntry entry = DbUtils.getSensorById(getItemId(position));
                entry.setIsSaving(String.valueOf(isChecked));
                entry.save();

                Log.d("DB_UTIL", "Is it for save?: " + entry.getIsSaving());

            }
        });
    }

    private class SensorItemHolder {
        TextView name;
        CheckBox plotCB;
        CheckBox saveCB;
    }
}
