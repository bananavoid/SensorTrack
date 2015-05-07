package com.asx.sensortrack;

import com.orm.SugarRecord;


/**
 * Model for sensors List
 */
public class SensorEntry extends SugarRecord<SensorEntry> {
    String name;
    float rate;
    String plotting;
    String saving;

    public SensorEntry(){
        //don't remove - orm requires
    }

    public SensorEntry(String name) {
        setName(name);
        setIsPlotting("false");
        setIsSaving("false");
        setRate(0);
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsPlotting() {
        return plotting;
    }

    public void setIsPlotting(String isPlotting) {
        this.plotting = isPlotting;
    }

    public String getIsSaving() {
        return saving;
    }

    public void setIsSaving(String isSaving) {
        this.saving = isSaving;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
