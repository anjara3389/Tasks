package proyectohabitos.example.neita.habitos.Task;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
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

    public Task(Integer id, String name, boolean d, boolean l, boolean m, boolean x, boolean j, boolean v, boolean s, Long sinceDate, Long reminder, Long chrono) {
        this.id = id;
        this.name = name;
        this.d = d;
        this.l = l;
        this.m = m;
        this.x = x;
        this.j = j;
        this.v = v;
        this.s = s;
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
        String sql = "SELECT id,name,d,l,m,x,j,v,s,since_date,reminder, chrono " +
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

    /*Consulta si una tarea se realizó el dia dado
    chrono es null si no tiene crono
    dateTime debe ser dado dandole la zona horaria correspondiente
     */
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
        String sql = "INSERT INTO span (activity_id,beg_date,end_date) VALUES (" + id + ",'" + DateUtils.getTimeOnCurrTimeZone(new Date()) + "','" + DateUtils.getTimeOnCurrTimeZone(new Date()) + "')";
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    //se descheckea una tarea sin crono (como no realizada en el día)
    public static void uncheckTask(int Id, SQLiteDatabase db) {
        String sql = "DELETE FROM span WHERE activity_id=" + Id + " AND CAST((beg_date/86400000) as int)=" + (int) (DateUtils.getTimeOnCurrTimeZone(new Date()) / 86400000);
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    //da la fecha y hora de la próxima alarma de una tarea
    public static Long getNextAlarm(SQLiteDatabase db, int taskId) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(new Date());
        int day = DateUtils.getDayInt(cal.getTime());
        Cursor c = db.rawQuery("SELECT d,l,m,x,j,v,s,reminder FROM activity WHERE id=" + taskId, null);

        if (c.moveToFirst()) {
            for (int i = 0; i < 8; i++) {
                if (c.getInt(day) == 1) {
                    //se le quita la fecha y solo se deja la hora
                    long rawDate = DateUtils.getTimeOnCurrTimeZoneDT(cal.getTimeInMillis());
                    long remindDate = DateUtils.trimDateLong(rawDate);//La fecha del reminder sin la hora
                    long remindDateTime = remindDate + DateUtils.getTimeOnCurrTimeZoneDT(c.getLong(7)); //fecha(remindDate) y hora(c.getLong(7))

                    if ((!cal.getTime().equals(cal2.getTime())) || ((cal.getTime().equals(cal2.getTime())) && (remindDateTime >= DateUtils.getTimeOnCurrTimeZone(new Date())))) {
                        return remindDateTime;
                    }
                }
                cal.add(Calendar.DAY_OF_YEAR, +1);//se incrementa la fecha del calendario en 1 día
                day = DateUtils.getDayInt(cal.getTime());
            }
        }
        return null;
    }

    /*Da una lista con trues y falses dependiendo si la tarea se realizó o no y debía realizarse en cada una de las fechas dentro del intervalo de tiempo entre begDate y endDate
     */
    public static ArrayList<Boolean> getDoneAndNotDone(Date begDate, Date endDate, int taskId, SQLiteDatabase db) {
        ArrayList<Boolean> doneAndNotDone = new ArrayList();
        Calendar begCal = DateUtils.getGregCalendar(DateUtils.trimDate(begDate));
        Calendar endCal = DateUtils.getGregCalendar(DateUtils.trimDate(endDate));

        Task task = new Task().select(db, taskId);
        ArrayList<Boolean> daysOfWeek = new ArrayList(Arrays.asList(task.d, task.l, task.m, task.x, task.j, task.v, task.s));
        while (!begCal.getTime().after(endCal.getTime())) {
            if (daysOfWeek.get(DateUtils.getDayInt(begCal.getTime()))) { //si la tarea tiene que hacerse el día
                doneAndNotDone.add(doneAndNotDone.size(), getIfTaskIsDoneDay(db, taskId, task.chrono, begCal.getTime().getTime())); //añade un true a la lista si la tarea se realizó, sino un false
            }
            begCal.add(Calendar.DAY_OF_YEAR, +1);//se incrementa la fecha del calendario en 1 día
        }
        return doneAndNotDone;
    }


    /*Da el porcentaje de realización de una tarea en un intervalo de tiempo dado
    si interv es 0 semanal,interv es 1 mensual,interv es 2 anual
    a dateTimese le debe dar la zona horaria correspondiente

    Si total es true significa que coje el porcentaje con respecto a toda la semana(hasta el domingo) o el mes completo(hasta el ultimo día del mes)
    Si total es false significa que coje el porcentaje hasta el día actual(el día de hoy)

    //CAAAAAAAAAMBIAAAAR .. LA SEMANA INICIA EN DOMINGO, HALLAR OTRA FORMA DE IDENTIFICAR LA SEMANA
     */
    public static double getStatistics(int taskId, int interv, boolean total, SQLiteDatabase db) {
        Task task = new Task().select(db, taskId);
        ArrayList<Boolean> doneAndNotDone;
        Date begD;
        int doneTasks = 0;
        Date nowDate = total == false ? new Date() : DateUtils.getLastDate(interv, new Date());

        Long nowDay = DateUtils.datePlusTZ(nowDate.getTime());//se le quita la hora y solo queda la fecha y se le suma la diferencia horaria. Día de hoy.
        Long sinceDay = DateUtils.datePlusTZ(task.sinceDate);//se le quita la hora y solo queda la fecha y se le suma la diferencia horaria. Día de en que se creó la tarea.

        Long since = DateUtils.getWeekMonthOrYear(sinceDay, interv);  //la semana(interv == 0),mes(interv == 1) o el año(interv == 2) en el que se creó la actividad
        Long now = DateUtils.getWeekMonthOrYear(nowDay, interv);  //la semana(interv == 0),mes(interv == 1) o el año(interv == 2) actual

        if (interv != 0 ? since.equals(now) : DateUtils.getIfDateIntoWeek(new Date(nowDay), new Date(sinceDay))) { //si la sem,mes o año en el que se creó la tarea == a la sem,mes o año actual
            begD = new Date();
            begD.setTime(sinceDay);
        } else {
            begD = DateUtils.getFirstDate(interv, nowDate); //begD es igual al primer día de la sem(interv == 0) o primer día del mes(interv == 1) o primer día del año interv == 2
        }
        doneAndNotDone = getDoneAndNotDone(begD, nowDate, task.id, db);

        //System.out.println(interv + "since///" + since);
        //System.out.println(interv + "now///" + now);
        //System.out.println(interv + "IGUALES///" + (since.equals(now)));
        //System.out.println(interv + "BEGD///" + begD);

        for (int i = 0; i < doneAndNotDone.size(); i++) {//Se saca el total de trues
            if (doneAndNotDone.get(i)) {
                doneTasks++;
            }
        }
        return doneAndNotDone.size() != 0 ? doneTasks * 100 / doneAndNotDone.size() : 0;
    }


}

