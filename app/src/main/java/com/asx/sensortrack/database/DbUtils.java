package com.asx.sensortrack.database;

import android.database.MatrixCursor;

import com.asx.sensortrack.SensorEntry;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public final class DbUtils {
    public static MatrixCursor getSensorsFullCursor() {
        List<SensorEntry> sensors = SensorEntry.listAll(SensorEntry.class); //use for get just all articles
        return getSensorsCursor(sensors);
    }

    public static MatrixCursor getSensorsSavingCursor() {
        List<SensorEntry> sensors = getSensorsSaving(); //use for get sensors for saving
        return getSensorsCursor(sensors);
    }

    public static MatrixCursor getSelectedSensorsCursor() {
        List<SensorEntry> sensors = getSelectedSensors(); //use for get sensors for saving
        return getSensorsCursor(sensors);
    }

    public static SensorEntry getSensorById(long id) {
        return SensorEntry.findById(SensorEntry.class, id);
    }

    public static List<SensorEntry> getSensorsSaving() {
        return Select.from(SensorEntry.class)
                .where(Condition.prop("saving").eq("true"))
                .list();
    }

    public static List<SensorEntry> getSelectedSensors() {
        return Select.from(SensorEntry.class)
                .where(Condition.prop("plotting").eq("true"))
                .or(Condition.prop("saving").eq("true"))
                .list();
    }

    public static List<SensorEntry> getSensorsPlotting() {
        return Select.from(SensorEntry.class)
                .where(Condition.prop("plotting").eq("true"))
                .list();
    }

    public static MatrixCursor getSensorsCursor(List<SensorEntry> sensors) {
        String[] c_columns = new String[] {
                "_id",
                "name",
                "type",
                "rate",
                "plotting",
                "saving"
        };
        MatrixCursor matrixCursor = new MatrixCursor(c_columns, 0);
        for (SensorEntry sensor : sensors) {
            matrixCursor.addRow(new Object[]{
                    sensor.getId().toString(),
                    sensor.getName(),
                    sensor.getType(),
                    sensor.getRate(),
                    sensor.getIsPlotting(),
                    sensor.getIsSaving()
            });
        }

        return matrixCursor;
    }
}
