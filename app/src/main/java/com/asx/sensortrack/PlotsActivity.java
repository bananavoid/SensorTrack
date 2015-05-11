package com.asx.sensortrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.asx.sensortrack.database.DbUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;
import java.util.List;


public class PlotsActivity extends ActionBarActivity {
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    //private ListView mList;
    private LinearLayout mList;

    private PlotsBaseAdapter mAdapter;
    private HashMap<Integer, GraphView> mGraphs = new HashMap<>();
    private HashMap<Integer, LineGraphSeries<DataPoint>> mDataSeries = new HashMap<>();
    private HashMap<Integer, LineGraphSeries<DataPoint>> mButtonDataSeries = new HashMap<>();
    private HashMap<Integer, Double> mLastXValue = new HashMap<>();
    private HashMap<Integer, Integer> mRates = new HashMap<>();


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
        final  String btnLabel = intent.getStringExtra("BTN_LABEL");
        double m = 0d;
        final double magnitude = intent.getDoubleExtra("MAGNITUDE", m);

        double lastX = ((double)mRates.get(type) + mLastXValue.get(type));
        mLastXValue.put(type, lastX);
        mDataSeries.get(type).appendData(new DataPoint(lastX, magnitude), false, 5);

//        Log.d("UPDATE_GRAPH", "BTN: " + btnLabel);
//
//        if (!btnLabel.equals(getString(R.string.default_btn_label))) {
//            double y = mDataSeries.get(type).getHighestValueY();
//            mButtonDataSeries.get(type).appendData(new DataPoint(lastX, y), false, 5);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(activityReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots);

        getSupportActionBar().setTitle(getString(R.string.plots_activity_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            LineGraphSeries<DataPoint> dataSeries = new LineGraphSeries<DataPoint>();
            //LineGraphSeries<DataPoint> btnSeries = new LineGraphSeries<DataPoint>();

            dataSeries.setColor(Color.BLUE);
            //btnSeries.setColor(Color.RED);

            View item = inflater.inflate(R.layout.plot_entry, null);
            GraphView graph = (GraphView)item.findViewById(R.id.graph);

            graph.addSeries(dataSeries);
            //graph.addSeries(btnSeries);

            graph.setTitle(entry.getName());
            graph.getViewport().setMinX(0);
            graph.getViewport().setMinY(0);

            mDataSeries.put(entry.getType(), dataSeries);
            //mButtonDataSeries.put(entry.getType(), btnSeries);
            mLastXValue.put(entry.getType(), 0d);
            mRates.put(entry.getType(), entry.getRate());

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
