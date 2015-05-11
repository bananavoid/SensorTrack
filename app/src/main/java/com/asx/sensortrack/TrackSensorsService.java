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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.asx.sensortrack.database.DbUtils;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class TrackSensorsService extends Service {

    private SensorManager mSensorManager;

    private HashMap<Integer, CSVWriter> mWriter = new HashMap<>();
    private HashMap<Integer, File> mFiles = new HashMap<>();
    private HashMap<Integer, Integer> mRates = new HashMap<>();
    private HashMap<Integer, String> mCurrentBtnIds = new HashMap<>();
    private HashMap<Integer, Boolean> mIsHeaderAdded = new HashMap<>();
    private HashMap<Integer, Boolean> mIsItSaved = new HashMap<>();
    private HashMap<Integer, Boolean> mIsItHandled = new HashMap<>();
    private HashMap<Integer, Boolean> mIsItPlotting = new HashMap<>();


    private ArrayList<Integer> mCurrentSensorsTypes = new ArrayList<Integer>();
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";
    private final Handler mHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        List<SensorEntry> sensorsList = DbUtils.getSelectedSensors();

        for (SensorEntry entry : sensorsList) {
            if (Boolean.valueOf(entry.getIsSaving())) {
                prepareFile(entry.getName(), entry.getType());
            }

            mCurrentSensorsTypes.add(entry.getType());
            int entryRate = entry.getRate();
            mRates.put(entry.getType(), entryRate);
            mCurrentBtnIds.put(entry.getType(), "null");
            mIsHeaderAdded.put(entry.getType(), false);
            mIsItSaved.put(entry.getType(), Boolean.valueOf(entry.getIsSaving()));
            mIsItPlotting.put(entry.getType(), Boolean.valueOf(entry.getIsPlotting()));
            mIsItHandled.put(entry.getType(), false);
        }

        Toast.makeText(this, getResources().getString(R.string.service_started), Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() != null) {
            String value = intent.getStringExtra("INPUT_DATA");

            for (int i = 0; i < mCurrentBtnIds.size() ;++i) {
                mCurrentBtnIds.put(mCurrentSensorsTypes.get(i), value);
            }
        }

        if (mSensorManager == null) {
            setUpSensors();
        }

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
            handleSensorEvent(null, event);
        }
    };

    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(final SensorEvent event) {
            int type = event.sensor.getType();
            if (!mIsItHandled.get(type)) {
                mIsItHandled.put(type, true);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleSensorEvent(event, null);
                        mHandler.removeCallbacks(this);
                    }
                }, mRates.get(type));
            }
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
                mSensorManager.registerListener(mListener, sensor, 2000);
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

    private void handleSensorEvent(SensorEvent event, TriggerEvent triggerEvent) {
        String[] finalValues = new String[]{};
        int type = 0;

        if (event != null) {
            if (event.values.length != 0) {
                type = event.sensor.getType();
                String[] values = new String[event.values.length + 2];

                for (int i = 0; i < event.values.length + 2; ++i) {
                    if (i == event.values.length + 1) {
                        String bi = mCurrentBtnIds.get(type);
                        values[i] = bi;
                        mCurrentBtnIds.put(type, "null");
                    } else if (i == 0) {
                        values[i] = String.valueOf(event.timestamp);
                    } else {
                        values[i] = Float.toString(event.values[i - 1]);
                    }
                }

                finalValues = values;
            }

        } else if (triggerEvent != null) {
            if (triggerEvent.values.length != 0) {
                type = triggerEvent.sensor.getType();
                String[] values = new String[triggerEvent.values.length + 2];

                for (int i = 0; i < triggerEvent.values.length + 2; ++i) {
                    if (i == triggerEvent.values.length + 1) {
                        String bi = mCurrentBtnIds.get(type);
                        values[i] = bi;
                        mCurrentBtnIds.put(type, getResources().getString(R.string.default_btn_label));
                    } else if (i == 0) {
                        values[i] = String.valueOf(triggerEvent.timestamp);
                    } else {
                        values[i] = Float.toString(triggerEvent.values[i - 1]);
                    }
                }

                finalValues = values;
            }
        }

        if (finalValues.length > 0 && type != 0) {
            if(mIsItPlotting.get(type)) {
                sendUpdateToUI(type, finalValues);
            }

            if(mIsItSaved.get(type)) {
                if (mFiles.get(type).length() == 0 && !mIsHeaderAdded.get(type)) {
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
                    mIsHeaderAdded.put(type, true);
                } else {
                    mWriter.get(type).writeNext(finalValues, false);
                }
            }
        }

        mIsItHandled.put(type, false);
    }

    private void sendUpdateToUI(int type, String[] values) {
        double magnitude;
        String buttonLabel = "";

        double squaresSumm = 0;

        for (int i = 1; i < values.length; ++i) {
            int lastValue = values.length - 1;
            if (i == lastValue) {
                buttonLabel = values[lastValue];
            } else {
                squaresSumm = +Math.pow(Double.parseDouble(values[i]), 2);
            }
        }

        magnitude = Math.sqrt(squaresSumm);

        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        new_intent.putExtra("TYPE", type);
        new_intent.putExtra("MAGNITUDE", magnitude);
        new_intent.putExtra("BTN_LABEL", buttonLabel);

        sendBroadcast(new_intent);
    }

    private String getCurrentTimestamp() {
        int time = (int) (System.currentTimeMillis());
        return String.valueOf(time);
    }
}