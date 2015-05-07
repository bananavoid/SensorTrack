package com.asx.sensortrack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.orm.SugarApp;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class SensorTrackApp extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        SugarRecord.deleteAll(SensorEntry.class);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : deviceSensors) {
            SensorEntry entry = new SensorEntry(sensor.getName());
            entry.save();

            long new_count = SensorEntry.count(SensorEntry.class, null, null);
            Log.d("DB_COUNT", "Real count: " + new_count);
        }
    }
}
