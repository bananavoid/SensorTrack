package com.asx.sensortrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.YPositionMetric;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.PointLabeler;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XValueMarker;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.asx.sensortrack.database.DbUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class PlotsActivity extends ActionBarActivity {
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    //private ListView mList;
    private LinearLayout mList;

    private HashMap<Integer, Double> mLastXValue = new HashMap<>();
    private HashMap<Integer, Integer> mRates = new HashMap<>();
    private String mCurrentButtonLabel;

    private HashMap<Integer, XYPlot> mGraphs = new HashMap<>();
    private static final int HISTORY_SENSOR_SIZE = 5;


    public String[] mPads = new String[] {
            "q",
            "w",
            "e",
            "r",
            "t",
            "y"
    };

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            updateGraph(intent);
        }
    };


    private void updateGraph(Intent intent) {
        int t = 0;
        final int type = intent.getIntExtra("TYPE", t);
        //final String btnLabel = intent.getStringExtra("BTN_LABEL");
        double m = 0d;
        final double magnitude = intent.getDoubleExtra("MAGNITUDE", m);

        final double lastX = ((double) mRates.get(type) + mLastXValue.get(type));
        mLastXValue.put(type, lastX);

        Object[] seriesSet = mGraphs.get(type).getSeriesSet().toArray();
        SimpleXYSeries series = (SimpleXYSeries)seriesSet[0];

        if (series.size() > HISTORY_SENSOR_SIZE) {
            series.removeFirst();
        }

        series.addLast(lastX, magnitude);

        if (!mCurrentButtonLabel.equals(getString(R.string.default_btn_label))) {
            XValueMarker xValMarker;
            xValMarker = new XValueMarker(lastX, mCurrentButtonLabel.toUpperCase());
            xValMarker.getTextPaint().setColor(Color.RED);

            YPositionMetric yPos = new YPositionMetric(100.0f, YLayoutStyle.ABSOLUTE_FROM_BOTTOM);

            xValMarker.setTextPosition(yPos);

            Collection<XYPlot> graphs = mGraphs.values();
            for (XYPlot plot : graphs) {
                plot.addMarker(xValMarker);
                //plot.redraw();
            }

            //mGraphs.get(type).addMarker(xValMarker);

            mCurrentButtonLabel = getString(R.string.default_btn_label);
        }

        mGraphs.get(type).redraw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
        unregisterReceiver(activityReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots);

        getSupportActionBar().setTitle(getString(R.string.plots_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentButtonLabel = getString(R.string.default_btn_label);

        if (activityReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(activityReceiver, intentFilter);
        }

        GridView grid = (GridView) findViewById(R.id.gridPad);
//        mList = (ListView) findViewById(R.id.listPlots);

        final List<SensorEntry> toPlottingEntries = DbUtils.getSensorsPlotting();
        mList = (LinearLayout)findViewById(R.id.scrollView);

        LayoutInflater inflater = this.getLayoutInflater();

            for (int i = 0; i < toPlottingEntries.size(); ++i) {
                SensorEntry entry = toPlottingEntries.get(i);

                View item = inflater.inflate(R.layout.plot_entry, null);
                XYPlot graph = (XYPlot)item.findViewById(R.id.graph);
                SimpleXYSeries xySeries = new SimpleXYSeries("Sensor data");

                LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                        Color.GREEN, null, null, null);
                formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
                formatter1.getLinePaint().setStrokeWidth(5);


                graph.addSeries(xySeries,
                        formatter1);

//                graph.addSeries(btnLevelSeries,
//                        new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));

                //graph.setDomainStepValue(entry.getRate());
                //graph.setTicksPerRangeLabel(3);
                //graph.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);

                graph.setDomainLabel("Time");
                graph.getDomainLabelWidget().pack();
                graph.setRangeLabel("Magnitude");
                graph.getRangeLabelWidget().pack();
                graph.setGridPadding(20, 0, 20, 0);

                mLastXValue.put(entry.getType(), 0d);
                mRates.put(entry.getType(), entry.getRate());
                mGraphs.put(entry.getType(), graph);

                mList.addView(item);
        }

//        mAdapter = new PlotsBaseAdapter(
//                this,
//                toPlottingEntries
//        );
//
//        mList.setAdapter(mAdapter);

        grid.setAdapter(new CustomGridAdapter(this, mPads));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                mCurrentButtonLabel = mPads[position];
                Intent intent = new Intent(getApplicationContext(), TrackSensorsService.class);
                intent.putExtra("INPUT_DATA", mPads[position]);
                startService(intent);
            }
        });

        Intent intent = new Intent(this, TrackSensorsService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plots, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop:
                stopTracking();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTracking();
        finish();
    }

    public void stopTracking() {
        Intent intent = new Intent(this, TrackSensorsService.class);
        stopService(intent);
    }
}
