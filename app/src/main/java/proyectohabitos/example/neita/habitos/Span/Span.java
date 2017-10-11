package proyectohabitos.example.neita.habitos.Span;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import proyectohabitos.example.neita.habitos.BaseHelper;

public class Span {
    public Integer id;
    public Long begDate;
    public Long endDate;
    public Integer activityId;

    public Span() {

    }

    public Span(Integer id, Long begDate, Long endDate, Integer activityId) {
        this.id = id;
        this.begDate = begDate;
        this.endDate = endDate;
        this.activityId = activityId;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();//content values es un contenedor de valores
        c.put("beg_date", begDate);
        c.put("end_date", endDate);
        c.put("activity_id", activityId);
        return c;
    }

    public Long insert(SQLiteDatabase db) {
        return db.insert("span", null, getValues());
    }

    public void update(SQLiteDatabase db, Integer id) {
        db.update("span", getValues(), " id=" + id + " ", null);
        BaseHelper.tryClose(db);
    }

    public Span select(SQLiteDatabase db, Integer id) {
        Cursor c = db.rawQuery("SELECT id,beg_date,end_date,activity_id " +
                "FROM span s " +
                "WHERE s.id=" + id + " ", null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            return new Span(c.getInt(0), c.getLong(1), c.getLong(2), c.getInt(3));
        }
        return null;
    }

    //devuelve un spam abierto si es que hubiere (con fecha de inicio pero sin fecha de fin)
    //si activityId no es=null busca los spans dentro de la actividad
    public static Span selectOpenedSpan(SQLiteDatabase db, Integer activityId) {
        Cursor c = db.rawQuery("SELECT s.id,s.beg_date,s.end_date,s.activity_id " +
                "FROM span s " +
                "WHERE s.end_date IS NULL " +
                (activityId != null ? "AND s.activity_id=" + activityId : ""), null);

        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            return new Span(c.getInt(0), c.getLong(1), c.getLong(2), c.getInt(3));
        }
        return null;
    }

    //Devuelve la suma de los tiempos de una actividad en el día dado
    public Long selectTotalTime(SQLiteDatabase db, Integer activityId, Long dateTime) {
        Long value = 0L;
        String q = "SELECT SUM(s.end_date-s.beg_date) " +
                "FROM span s " +
                "WHERE s.activity_id=" + activityId + " " +
                "AND CAST((s.beg_date/86400000) as int)=" + (int) (dateTime / 86400000);

        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            value = c.getLong(0);
        }
        return value;
    }

}
