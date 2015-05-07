package com.asx.sensortrack;

import com.orm.SugarRecord;


/**
 * Model for sensors List
 */
public class SensorEntry extends SugarRecord<SensorEntry> {
    String name;
    int type;
    float rate;
    String plotting;
    String saving;

    public SensorEntry(){
        //don't remove - orm requires
    }

    public SensorEntry(String name, int type) {
        setName(name);
        setType(type);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
