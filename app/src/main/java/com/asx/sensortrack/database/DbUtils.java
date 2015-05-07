package com.asx.sensortrack.database;

import android.database.MatrixCursor;

import com.asx.sensortrack.SensorEntry;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public final class DbUtils {
    public static MatrixCursor getSensorsFullCursor() {
        List<SensorEntry> sensors = SensorEntry.listAll(SensorEntry.class); //use for get just all articles
        //List<SensorEntry> articles = getArticlesSortedByChangedDate(); //use for get sorted articles
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

    public static MatrixCursor getSensorsSelectedCursor() {
        List<SensorEntry> sensors = getSensorsSaving(); //use for get sorted articles
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
//
//    public static List<Article> getArticlesSortedByChangedDate() {
//        return Article.find(Article.class, null, null, null, "changed", null);
//    }
//
    public static SensorEntry getSensorById(long id) {
        return SensorEntry.findById(SensorEntry.class, id);
    }
//
//    public static List<SensorEntry> getSensorsSaving() {
//        return SensorEntry.findWithQuery(SensorEntry.class, "Select * from SensorEntry where getIsSaving = ?", String.valueOf(true));
//    }

    public static List<SensorEntry> getSensorsSaving() {

        return Select.from(SensorEntry.class)
                .where(Condition.prop("saving").eq("true"))
                .list();
    }
}
