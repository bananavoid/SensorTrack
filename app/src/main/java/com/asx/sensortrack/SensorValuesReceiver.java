package com.asx.sensortrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kosmokapusta on 09.05.15.
 */
public class SensorValuesReceiver extends BroadcastReceiver {
    public static final String CUSTOM_INTENT = "jTEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent();
        i.setAction(CUSTOM_INTENT);
        context.sendBroadcast(i);
    }
}
