package com.asx.sensortrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.asx.sensortrack.database.DbUtils;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class TrackSensorsService extends Service {

    private SensorManager mSensorManager;
    private Context mContext;

    private HashMap<Integer, CSVWriter> mWriter = new HashMap<>();
    private HashMap<Integer, File> mFileToRecord = new HashMap<>();
    private ArrayList<Integer> mCurrentSensorsTypes = new ArrayList<Integer>();

    String axisX;
    String axisY;
    String axisZ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        List<SensorEntry> sensorsList = DbUtils.getSensorsSaving();

        for (SensorEntry entry : sensorsList) {
            prepareFile(entry.getName(), entry.getType());
            mCurrentSensorsTypes.add(entry.getType());
        }

        Toast.makeText(this, getResources().getString(R.string.service_started), Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpSensors();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < mWriter.size(); ++i) {
            if(mWriter.get(mCurrentSensorsTypes.get(i)) != null) {
                try {
                    mWriter.get(mCurrentSensorsTypes.get(i)).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }

        Toast.makeText(this, getResources().getString(R.string.service_stopped), Toast.LENGTH_LONG).show();
    }

    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            int type = event.sensor.getType();

            axisX = Float.toString(event.values[0]);
            axisY = Float.toString(event.values[1]);
            axisZ = Float.toString(event.values[2]);

            mWriter.get(type).writeNext(new String[]{axisX, axisY, axisZ}, false);

//            Double magnitude = Math.sqrt((Math.pow(Double.parseDouble(axisX), 2)) +
//                    (Math.pow(Double.parseDouble(axisY), 2)) +
//                    (Math.pow(Double.parseDouble(axisZ), 2)));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void setUpSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        for (Integer type : mCurrentSensorsTypes) {
            Sensor sensor = mSensorManager.getDefaultSensor(type);
            mSensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void prepareFile(String name, int type) {
        File fileDir = (Environment.getExternalStorageDirectory());

        File templatesDirectory= new File(fileDir + "/SensorsData");
        if (templatesDirectory.exists()) {
            File[] files = templatesDirectory.listFiles();
            for (int i = 0; i < files.length; ++i) {
                files[i].delete();
            }
        }

        templatesDirectory.mkdirs();

        File file = new File(templatesDirectory, name + ".csv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFileToRecord.put(type, file);

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            mWriter.put(type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}