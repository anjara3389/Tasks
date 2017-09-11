package proyectohabitos.example.neita.habitos.Span;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

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

    public void update(SQLiteDatabase db, Long id) {
        db.update("span", getValues(), " id=" + id + " ", null);
        BaseHelper.tryClose(db);
    }

    public Long selectLastTime(SQLiteDatabase db, Integer activityId) {
        Long value = 0L;
        String q = "SELECT SUM(s.end_date-s.beg_date) " +
                "FROM span s " +
                "WHERE s.activity_id=" + activityId + " " +
                "AND CAST((s.beg_date/86400000) as int)=" + (int) (new Date().getTime() / 86400000);

        Cursor c = db.rawQuery(q, null);

        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            //System.out.println("RECUPERAS");
            //System.out.println(c.getLong(0));
            value = c.getLong(0);
        }
        return value;
    }

}
