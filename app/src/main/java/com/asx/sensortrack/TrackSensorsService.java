package com.asx.sensortrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.asx.sensortrack.database.DbUtils;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TrackSensorsService extends Service {

    private SensorManager mSensorManager;

    private HashMap<Integer, CSVWriter> mWriter = new HashMap<>();
    private HashMap<Integer, File> mFiles = new HashMap<>();
    private HashMap<Integer, Integer> mRates = new HashMap<>();
    private ArrayList<Integer> mCurrentSensorsTypes = new ArrayList<Integer>();

    private boolean isHeaderAdded = false;

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
            int entryRate = entry.getRate() == 0 ? SensorManager.SENSOR_DELAY_NORMAL : entry.getRate();
            mRates.put(entry.getType(), entryRate);
        }

        Toast.makeText(this, getResources().getString(R.string.service_started), Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras()!= null) {
            String value = intent.getStringExtra("INPUT_DATA");
            for (int i = 0; i < mWriter.size(); ++i) {
                mWriter.get(mCurrentSensorsTypes.get(i)).writeNext(new String[]{value}, false);
            }
        }

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

        if (mTriggerListener != null && mSensorManager != null) {
            Sensor sigMotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
            mSensorManager.cancelTriggerSensor(mTriggerListener, sigMotion);
        }

        Toast.makeText(this, getResources().getString(R.string.service_stopped), Toast.LENGTH_LONG).show();
    }

    private final TriggerEventListener mTriggerListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            int type = event.sensor.getType();

            String[] values = new String[event.values.length];

            for(int i = 0; i < event.values.length; ++i) {
                values[i] = Float.toString(event.values[i]);
            }

            mWriter.get(type).writeNext(values, false);
        }
    };

    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            handleSensorEvent(event);
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
            if (type != 17) {
                mSensorManager.registerListener(mListener, sensor, mRates.get(type));
            } else {
                mSensorManager.requestTriggerSensor(mTriggerListener, sensor);
            }
        }
    }

    public void prepareFile(String name, int type) {
        File fileDir = (Environment.getExternalStorageDirectory());

        File templatesDirectory= new File(fileDir + "/SensorsData");
        if (!templatesDirectory.exists()) {
            templatesDirectory.mkdirs();
        }

        File file = new File(templatesDirectory, name + getCurrentTimestamp() + ".csv");
        mFiles.put(type, file);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file, true));
            mWriter.put(type, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSensorEvent(SensorEvent event) {
        if (event.values.length != 0) {
            int type = event.sensor.getType();

            String[] values = new String[event.values.length + 2];

            if (mFiles.get(type).length() == 0 && !isHeaderAdded) {
                String[] headerValues = new String[event.values.length + 2];
                for(int i = 0; i < event.values.length + 2; ++i) {
                    if (i == 0) {
                        headerValues[i] = "timestamp";
                    } else if (i == event.values.length + 1) {
                        headerValues[i] = "buttonId";
                    } else {
                        headerValues[i] = "value_" + i;
                    }
                }

                mWriter.get(type).writeNext(headerValues);
                isHeaderAdded = true;
            } else {
                for(int i = 0; i < event.values.length + 2; ++i) {
                    if (i == event.values.length + 1) {
                        values[i] = "Nold";
                    } else if (i == 0) {
                        values[i] = String.valueOf(event.timestamp);
                    } else {
                        values[i] = Float.toString(event.values[i - 1]);
                    }
                }

                mWriter.get(type).writeNext(values, false);
            }
        }
    }

    private String getCurrentTimestamp() {
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);
        String ts =  tsTemp.toString();

        return ts;
    }
}