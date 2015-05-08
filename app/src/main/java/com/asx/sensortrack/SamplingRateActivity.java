package com.asx.sensortrack;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.asx.sensortrack.database.DbUtils;


public class SamplingRateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_rate);

        getSupportActionBar().setTitle("Choose sampling rate in milliseconds");

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
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_next:
                doNext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, TrackSensorsService.class);
        stopService(intent);
    }

    public void doNext() {
        Intent intent = new Intent(getApplicationContext(), TrackSensorsService.class);
        startService(intent);

        Intent nextActivity = new Intent(this, PlotsActivity.class);
        startActivity(nextActivity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, TrackSensorsService.class);
        stopService(intent);
    }
}
