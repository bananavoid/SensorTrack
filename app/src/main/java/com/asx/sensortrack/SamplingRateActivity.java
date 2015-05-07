package com.asx.sensortrack;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.List;


public class SamplingRateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_rate);

        ListView list = (ListView) findViewById(R.id.sensorsListSampling);

        LayoutInflater headerInflater = this.getLayoutInflater();
        View header = headerInflater.inflate(R.layout.sensors_list_rate_header, null);

        list.addHeaderView(header);

        list.setAdapter(new SelectedSensorsCursorAdapter(
                this,
                DbUtils.getSensorsSelectedCursor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
}
