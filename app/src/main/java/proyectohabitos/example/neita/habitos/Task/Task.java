package proyectohabitos.example.neita.habitos.Task;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateOnTimeZone;
import proyectohabitos.example.neita.habitos.Services.AlarmNotification.ServiceAlarmNotification;
import proyectohabitos.example.neita.habitos.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.Span.Span;

public class Task {
    public Integer id;
    public String name;
    public boolean l;
    public boolean m;
    public boolean x;
    public boolean j;
    public boolean v;
    public boolean s;
    public boolean d;
    public Long sinceDate;
    public Long reminder;
    public Long chrono;

    public Task() {

    }

    public Task(Integer id, String name, boolean l, boolean m, boolean x, boolean j, boolean v, boolean s, boolean d, Long sinceDate, Long reminder, Long chrono) {
        this.id = id;
        this.name = name;
        this.l = l;
        this.m = m;
        this.x = x;
        this.j = j;
        this.v = v;
        this.s = s;
        this.d = d;
        this.sinceDate = sinceDate;
        this.reminder = reminder;
        this.chrono = chrono;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();//content values es un contenedor de valores
        c.put("name", name);
        c.put("l", l);
        c.put("m", m);
        c.put("x", x);
        c.put("j", j);
        c.put("v", v);
        c.put("s", s);
        c.put("d", d);
        c.put("since_date", sinceDate);
        c.put("reminder", reminder);
        c.put("chrono", chrono);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("activity", null, getValues());
    }

    public void update(SQLiteDatabase db, Integer id) {
        db.update("activity", getValues(), " id=" + id + " ", null);
        BaseHelper.tryClose(db);
    }

    public Task select(SQLiteDatabase db, Integer id) {
        String sql = "SELECT id,name,l,m,x,j,v,s,d,since_date,reminder, chrono " +
                "FROM activity " +
                "WHERE id=" + id;

        Cursor c = db.rawQuery(sql, null);
        Task task = new Task();
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            task = new Task(id, c.getString(1), c.getInt(2) == 1, c.getInt(3) == 1, c.getInt(4) == 1, c.getInt(5) == 1, c.getInt(6) == 1, c.getInt(7) == 1, c.getInt(8) == 1, c.getLong(9), c.getLong(10), c.isNull(11) ? null : c.getLong(11));
        }

        return task;
    }

    public static void delete(int id, SQLiteDatabase db, Context c) {
        //Se cancela la programación de los servicios
        GcmNetworkManager gcmManag = GcmNetworkManager.getInstance(c);
        gcmManag.cancelTask(ServiceAlarmNotification.REMIND + id, ServiceAlarmNotification.class);
        gcmManag.cancelTask(ServiceChrNotification.CHRON + id, ServiceAlarmNotification.class);
        //Se eliminan los spans
        db.execSQL("DELETE FROM span WHERE activity_id=" + id);
        //se elimina la actividad
        db.execSQL("DELETE FROM activity WHERE id=" + id);
        BaseHelper.tryClose(db);
    }

    //Consulta si una tarea se realizó el dia dado
    //chrono es null si no tiene crono
    public static Boolean getIfTaskIsDoneDay(SQLiteDatabase db, int activityId, Long chrono, Long dateTime) {
        if (chrono == null) { //si no tiene crono
            Cursor c = db.rawQuery("SELECT COUNT(*)>0 " +
                    "FROM span s " +
                    "WHERE s.activity_id=" + activityId + " AND CAST((s.beg_date/86400000) as int)=" + (int) (dateTime / 86400000), null);
            if (c.moveToFirst()) {//si hay datos
                return c.getInt(0) == 1;
            }
        } else {  //si tiene crono
            return new Span().selectTotalTime(db, activityId, dateTime) >= chrono * 60 * 1000;
        }
        return null;
    }

    //se checkea una tarea sin crono como realizada en el día
    public static void checkTaskAsDone(int id, SQLiteDatabase db) {
        String sql = "INSERT INTO span (activity_id,beg_date,end_date) VALUES (" + id + ",'" + DateOnTimeZone.getTimeOnCurrTimeZone(new Date()) + "','" + DateOnTimeZone.getTimeOnCurrTimeZone(new Date()) + "')";
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    //se descheckea una tarea sin crono (como no realizada en el día)
    public static void uncheckTask(int Id, SQLiteDatabase db) {
        String sql = "DELETE FROM span WHERE activity_id=" + Id + " AND CAST((beg_date/86400000) as int)=" + (int) (DateOnTimeZone.getTimeOnCurrTimeZone(new Date()) / 86400000);
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }


    public static String getDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? "l" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? "m" :
                (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? "x" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? "j" :
                        (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? "v" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? "s" : "d")))));
    }

    public static int getDayInt(Date date) {
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(date);
        return cal2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? 0 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? 1 : //para saber qué día es la fecha en i
                (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? 2 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? 3 :
                        (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? 4 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? 5 : 6)))));
    }

    //da la fecha y hora de la proxima alarma de una tarea
    public static Long getNextAlarm(SQLiteDatabase db, int taskId) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(new Date());
        int day = getDayInt(cal.getTime());
        Cursor c = db.rawQuery("SELECT l,m,x,j,v,s,d,reminder FROM activity WHERE id=" + taskId, null);

        if (c.moveToFirst()) {
            for (int i = 0; i < 8; i++) {
                if (c.getInt(day) == 1) {

                    long rawDate = DateOnTimeZone.getTimeOnCurrTimeZoneDT(cal.getTimeInMillis());
                    long remindDateTime = (rawDate - (rawDate % (24 * 60 * 60 * 1000)) + DateOnTimeZone.getTimeOnCurrTimeZoneDT(c.getLong(7)));

                    if ((!cal.getTime().equals(cal2.getTime())) || ((cal.getTime().equals(cal2.getTime())) && (remindDateTime >= DateOnTimeZone.getTimeOnCurrTimeZone(new Date())))) {
                        return remindDateTime;
                    }
                }
                cal.add(Calendar.DAY_OF_YEAR, +1);
                day = getDayInt(cal.getTime());
            }
        }
        return null;
    }
}

