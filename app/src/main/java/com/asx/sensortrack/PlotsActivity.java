package com.asx.sensortrack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.asx.sensortrack.database.DbUtils;

import java.util.List;


public class PlotsActivity extends ActionBarActivity {

    public String[] mPads = new String[] {
            "q",
            "w",
            "e",
            "r",
            "t",
            "y"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots);

        getSupportActionBar().setTitle("Plotting selected sensors");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView grid = (GridView) findViewById(R.id.gridPad);
        final ListView list = (ListView) findViewById(R.id.listPlots);

        final List<SensorEntry> toPlottingEntries = DbUtils.getSensorsPlotting();
        final String[] names = new String[toPlottingEntries.size()];

        for(int i = 0; i< toPlottingEntries.size(); ++i) {
            names[i] = toPlottingEntries.get(i).getName();
        }

        list.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.test,
                names
        ));

        grid.setAdapter(new CustomGridAdapter());

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), TrackSensorsService.class);
                intent.putExtra("INPUT_DATA", mPads[position]);
                startService(intent);

                for(int i = 0; i< names.length; ++i) {
                    names[i] = toPlottingEntries.get(i).getName() + "   " + mPads[position];
                }

                list.setAdapter(new ArrayAdapter<String>(
                        getBaseContext(),
                        R.layout.test,
                        names
                ));
            }
        });
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
    }

    public class CustomGridAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public CustomGridAdapter() {
            inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void stopTracking() {
        Intent intent = new Intent(this, TrackSensorsService.class);
        stopService(intent);
    }
}
