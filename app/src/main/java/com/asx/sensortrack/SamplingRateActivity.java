package com.asx.sensortrack;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.asx.sensortrack.database.DbUtils;

import java.util.ArrayList;
import java.util.List;


public class SamplingRateActivity extends ActionBarActivity {
    private LinearLayout mList;
    private List<SensorEntry> mEntries;
    private ArrayList<EditText> mEdits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_rate);

        getSupportActionBar().setTitle("Choose sampling rate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater headerInflater = this.getLayoutInflater();
        View header = headerInflater.inflate(R.layout.sensors_list_rate_header, null);

        mEntries = DbUtils.getSelectedSensors();

        mList = (LinearLayout)findViewById(R.id.scrollView);
        mList.addView(header);

        for (int i = 0; i < mEntries.size(); ++i) {
            SensorEntry entry = mEntries.get(i);
            View item = headerInflater.inflate(R.layout.sensors_list_rate_entry, null);
            TextView t = (TextView)item.findViewById(R.id.sensorName);
            EditText e = (EditText)item.findViewById(R.id.rateEdit);

            mEdits.add(e);

            t.setText(entry.getName());
            e.setText(String.valueOf(entry.getRate()));

            mList.addView(item);
        }
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
        cleanAll();
    }

    public void doNext() {
        if (mList.getChildCount() > 1 ) {
            saveRates();

            Intent intent = new Intent(getApplicationContext(), TrackSensorsService.class);
            startService(intent);

            Intent nextActivity = new Intent(this, PlotsActivity.class);
            startActivity(nextActivity);
        } else {
            Toast.makeText(this, "Sorry, no sensors to track", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cleanAll();
    }

    public void cleanAll() {
        Intent intent = new Intent(this, TrackSensorsService.class);
        stopService(intent);
        finish();
    }

    private void saveRates(){
        for (int i = 0; i < mEdits.size(); ++i) {
            EditText item = mEdits.get(i);
            int rate = Integer.valueOf(item.getText().toString());
            mEntries.get(i).setRate(rate);
            mEntries.get(i).save();
        }
    }
}
