package com.asx.sensortrack;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;
import java.util.List;


public class PlotsBaseAdapter extends BaseAdapter {
    private List<SensorEntry> mEntries;
    private Context mContext;
    private LayoutInflater mInflater;
    private HashMap<Integer, Double> mLastXValue = new HashMap<>();
    private HashMap<Integer, Integer> mRates = new HashMap<>();
    private HashMap<Integer, LineGraphSeries<DataPoint>> mDataSeries = new HashMap<>();
    private HashMap<Integer, LineGraphSeries<DataPoint>> mButtonDataSeries = new HashMap<>();
    private HashMap<Integer, Boolean> mInitialized = new HashMap<>();

    //private boolean mInitialized = false;
    private double lastX = 0d;

    public PlotsBaseAdapter(Context context, List<SensorEntry> entries) {
        this.mEntries = entries;
        this.mContext = context;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(this.mContext.LAYOUT_INFLATER_SERVICE);

        for(SensorEntry entry : mEntries) {
            LineGraphSeries<DataPoint> dataSeries = new LineGraphSeries<DataPoint>();
            LineGraphSeries<DataPoint> btnSeries = new LineGraphSeries<DataPoint>();

            dataSeries.setColor(Color.RED);
            btnSeries.setColor(Color.BLUE);

            mDataSeries.put(entry.getType(), dataSeries);
            mButtonDataSeries.put(entry.getType(), btnSeries);
            mRates.put(entry.getType(), entry.getRate());
            mInitialized.put(entry.getType(), false);
            mLastXValue.put(entry.getType(), 0d);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlotItemHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.plot_entry, null);
            holder = new PlotItemHolder();
            convertView.setTag(holder);
        } else {
            holder = (PlotItemHolder) convertView.getTag();
        }

        holder.graph = (GraphView)convertView.findViewById(R.id.graph);

        if (!mInitialized.get(getItem(position).getType())) {
            holder.graph.addSeries(mDataSeries.get(getItem(position).getType()));
            holder.graph.addSeries(mButtonDataSeries.get(getItem(position).getType()));
            holder.graph.getViewport().setMinX(0);
            holder.graph.getViewport().setMinY(0);
//            holder.graph.getViewport().setMaxX(500);
//            holder.graph.getViewport().setXAxisBoundsManual(true);

            mInitialized.put(getItem(position).getType(), true);
        }

        return convertView;
    }

    private class PlotItemHolder {
        GraphView graph;
        long ref;
    }

    public void setGraphData(int type, double magnitude, String btnLabel) {
//        Log.d("setGraphData", "timestamp: "+timestamp);

        double lastX = ((double)mRates.get(type) + mLastXValue.get(type));
        mLastXValue.put(type, lastX);

        LineGraphSeries<DataPoint> series = mDataSeries.get(type);

        series.appendData(new DataPoint(lastX, magnitude), false, 500);

//        Log.d("setGraphData", "LAST X: "+xValue);

//        if (!btnLabel.equals("null")) {
//            mButtonDataSeries.get(type).appendData(new DataPoint(0, 1), true, 40);
//        }
//        notifyDataSetChanged();
    }
}
