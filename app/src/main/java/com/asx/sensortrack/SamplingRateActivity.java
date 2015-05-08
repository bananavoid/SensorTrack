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
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_rate);

        getSupportActionBar().setTitle("Choose sampling rate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = (ListView) findViewById(R.id.sensorsListSampling);

        LayoutInflater headerInflater = this.getLayoutInflater();
        View header = headerInflater.inflate(R.layout.sensors_list_rate_header, null);

        mList.addHeaderView(header);

        mList.setAdapter(new SelectedSensorsCursorAdapter(
                this,
                DbUtils.getSelectedSensorsCursor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
//        for (int i = 0; i <mList.getCount(); ++i) {
//            long id = mList.getAdapter().getItemId(i);
//            float rate = mList.getAdapter()
//            SensorEntry entry = DbUtils.getSensorById(id);
//        }

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
