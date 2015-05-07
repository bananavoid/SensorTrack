package com.asx.sensortrack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    static final String TAG = MainActivity.class.getSimpleName();

    ListView mSensorsList;
    SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorsList = (ListView) findViewById(R.id.sensorsList);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ArrayList<SensorEntry> sensors = new ArrayList<>();
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : deviceSensors) {
            sensors.add(new SensorEntry(sensor.getName()));
        }

        LayoutInflater headerInflater = this.getLayoutInflater();
        View header = headerInflater.inflate(R.layout.sensors_list_header, null);

        mSensorsList.addHeaderView(header);
        mSensorsList.setAdapter(new SensorsListArrayAdapter(this, R.layout.sensors_list_entry, sensors));
    }

    public void doNext(View view) {

    }
}
