package proyectohabitos.example.neita.habitos.Span;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateOnTZone;

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

    public Span selectCurrentSpan(SQLiteDatabase db, int activityId) {
       /* Format f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor d= db.rawQuery("SELECT s.id,s.beg_date,s.end_date " +
                "FROM span s " +
                "WHERE s.activity_id=" + activityId , null);
        if(d.moveToFirst()){
            do{
                System.out.println(d.getInt(0)+f.format(d.getLong(1))+"-"+f.format(d.getLong(2)));
            }while(d.moveToNext());
        }
        else{
            System.out.println("NOPE");
        }*/

        Cursor c = db.rawQuery("SELECT s.id,s.beg_date,s.end_date,s.activity_id " +
                "FROM span s " +
                "WHERE s.end_date IS NULL " +
                "AND s.activity_id=" + activityId, null);

        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            return new Span(c.getInt(0), c.getLong(1), c.getLong(2), c.getInt(3));
        }
        return null;
    }

    //La suma de los tiempos de la actividad en el día
    public Long selectLastTime(SQLiteDatabase db, Integer activityId, Date date) {
        Long value = 0L;
        String q = "SELECT SUM(s.end_date-s.beg_date) " +
                "FROM span s " +
                "WHERE s.activity_id=" + activityId + " " +
                "AND CAST((s.beg_date/86400000) as int)=" + (int) ((date != null ? date.getTime() : DateOnTZone.getTimeOnCurrTimeZone()) / 86400000);

        Cursor c = db.rawQuery(q, null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            value = c.getLong(0);
        }
        return value;
    }

}
