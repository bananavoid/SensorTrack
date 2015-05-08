package com.asx.sensortrack;

import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.asx.sensortrack.database.DbUtils;

import java.util.HashMap;


public class SelectedSensorsCursorAdapter extends CursorAdapter {
    private final Cursor mCursor;
    private Context mContext;
//    private int mRowIdColumn;
    private LayoutInflater mInflater;
    //private HashMap<Integer, String> textValues = new HashMap<Integer, String>();

    public SelectedSensorsCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        this.mContext = context;
        this.mCursor = c;

        //boolean cursorPresent = c != null;
        //mRowIdColumn = cursorPresent ? this.mCursor.getColumnIndexOrThrow("_id") : -1;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        View rowlayout = this.mInflater.inflate(R.layout.sensors_list_rate_entry, null);
        final SensorItemHolder viewHolder;
        viewHolder = new SensorItemHolder();

        viewHolder.name = (TextView)rowlayout.findViewById(R.id.sensorName);
        viewHolder.rateEdit = (EditText)rowlayout.findViewById(R.id.rateEdit);

        final int rowId = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));

        viewHolder.rateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    SensorEntry entry = DbUtils.getSensorById(rowId);
                    entry.setRate(Integer.parseInt(s.toString()));
                    entry.save();
                }
            }
        });

        rowlayout.setTag(viewHolder);

        return rowlayout;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
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
    public void bindView(View view, Context context, Cursor cursor) {
        final SensorItemHolder holder = (SensorItemHolder) view.getTag();

        holder.name.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
    }

    private class SensorItemHolder {
        TextView name;
        EditText rateEdit;
    }
}
