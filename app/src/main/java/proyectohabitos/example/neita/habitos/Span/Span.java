package proyectohabitos.example.neita.habitos.Span;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.SQLiteQuery;

//Los lapsos de tiempo en los que se realiza la tarea cuando tiene cronómetro
//cuando la tarea no tiene cronómetro, al marcarla se creará un nuevo spam con misma fecha de inicio y fin
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

    public Span select(SQLiteDatabase db, Integer id) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT id,beg_date,end_date,activity_id " +
                "FROM span s " +
                "WHERE s.id=" + id + " ");
        Object[] o = sq.getRecord(db);
        if (o != null) {
            for (int i = 0; i < o.length; i++) {
                return new Span(sq.getAsInteger(o[0]), sq.getAsLong(o[1]), sq.getAsLong(o[2]), sq.getAsInteger(o[3]));
            }
        }
        return null;
    }

    //devuelve un spam abierto si es que hubiere (con fecha de inicio pero sin fecha de fin)
    //si activityId no es=null busca los spans dentro de la actividad
    public static Span selectOpenedSpan(SQLiteDatabase db, Integer activityId) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT s.id,s.beg_date,s.end_date,s.activity_id " +
                "FROM span s " +
                "WHERE s.end_date IS NULL " +
                (activityId != null ? "AND s.activity_id=" + activityId : ""));

        Object[] o = sq.getRecord(db);
        if (o != null && o.length > 0) {
            for (int i = 0; i < o.length; i++) {
                return new Span(sq.getAsInteger(o[0]), sq.getAsLong(o[1]), sq.getAsLong(o[2]), sq.getAsInteger(o[3]));
            }
        }
        return null;
    }

    //Devuelve la suma de los tiempos de una actividad en el día dado
    public Long selectTotalTime(SQLiteDatabase db, Integer activityId, Long dateTime) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT SUM(s.end_date-s.beg_date) " +
                "FROM span s " +
                "WHERE s.activity_id=" + activityId + " " +
                "AND CAST((s.beg_date/86400000) as int)=" + (int) (dateTime / 86400000));
        Long value = sq.getLong(db);
        if (value != null) {
            return value;
        }
        return 0L;
    }
}
