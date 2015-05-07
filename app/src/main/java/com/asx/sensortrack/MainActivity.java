package com.asx.sensortrack;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list = (ListView) findViewById(R.id.sensorsList);
        //List<SensorEntry> sensors = SensorEntry.listAll(SensorEntry.class);

        LayoutInflater headerInflater = this.getLayoutInflater();
        View header = headerInflater.inflate(R.layout.sensors_list_header, null);

        list.addHeaderView(header);
        //mSensorsList.setAdapter(new SensorsListArrayAdapter(this, sensors));
        list.setAdapter(new FullSensorsCursorAtapter(
                this,
                DbUtils.getSensorsFullCursor()));
    }

    public void doNext(View view) {
        Intent intent = new Intent(this, SamplingRateActivity.class);
        startActivity(intent);
    }
}
