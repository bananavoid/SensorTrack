package com.asx.sensortrack;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.orm.SugarRecord;


/**
 * Model for sensors List
 */
public class SensorEntry extends SugarRecord<SensorEntry> {
    String name;
    boolean isPlotting;
    boolean isSaving;

    public SensorEntry(){
        //don't remove - orm requires
    }

    public SensorEntry(String name) {
        setName(name);
        setIsPlotting(false);
        setIsSaving(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlotting() {
        return isPlotting;
    }

    public void setIsPlotting(boolean isPlotting) {
        this.isPlotting = isPlotting;
    }

    public boolean isSaving() {
        return isSaving;
    }

    public void setIsSaving(boolean isSaving) {
        this.isSaving = isSaving;
    }
}
