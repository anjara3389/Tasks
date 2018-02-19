package proyectohabitos.example.neita.habitos.Task;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Reminder.AlarmNotification.Services.ServiceAlarmNotification;
import proyectohabitos.example.neita.habitos.Result.Result;
import proyectohabitos.example.neita.habitos.SQLiteQuery;
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
    /*Fecha de creación de la tarea*/
    public Date sinceDate;
    /*Hora de recordatorio para realizar la tarea*/
    public Date reminder;
    /*Tiempo en minutos que se utilizará para realizar la tarea con el cronómetro*/
    public Long chrono;

    public Task() {

    }

    public Task(Integer id, String name, boolean d, boolean l, boolean m, boolean x, boolean j, boolean v, boolean s, Date sinceDate, Date reminder, Long chrono) {
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
        c.put("since_date", SQLiteQuery.dateTimeFormat.format(sinceDate));
        c.put("reminder", reminder != null ? SQLiteQuery.dateTimeFormat.format(reminder) : null);
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

    public Task select(SQLiteDatabase db, Integer id) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT name,d,l,m,x,j,v,s,since_date,reminder,chrono " +
                "FROM activity " +
                "WHERE id=" + id);
        Object[] data = sq.getRecord(db);
        if (data != null) {
            return new Task(id, sq.getAsString(data[0]), sq.getAsInteger(data[1]) == 1, sq.getAsInteger(data[2]) == 1, sq.getAsInteger(data[3]) == 1, sq.getAsInteger(data[4]) == 1, sq.getAsInteger(data[5]) == 1, sq.getAsInteger(data[6]) == 1, sq.getAsInteger(data[7]) == 1, SQLiteQuery.dateTimeFormat.parse(sq.getAsString(data[8])), sq.getAsString(data[9]) != null ? SQLiteQuery.dateTimeFormat.parse(sq.getAsString(data[9])) : null, data[10] == null ? null : sq.getAsLong(data[10]));
        }
        return null;
    }

    public static ArrayList<Task> getTasks(SQLiteDatabase db) throws Exception {
        ArrayList<Task> tasks = new ArrayList();

        SQLiteQuery sq = new SQLiteQuery("SELECT id,name,d,l,m,x,j,v,s,since_date,reminder,chrono " +
                "FROM activity ");
        Object[][] data = sq.getRecords(db);
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                tasks.add(new Task(sq.getAsInteger(data[i][0]), (String) data[i][1], sq.getAsInteger(data[i][2]) == 1, sq.getAsInteger(data[i][3]) == 1, sq.getAsInteger(data[i][4]) == 1, sq.getAsInteger(data[i][5]) == 1, sq.getAsInteger(data[i][6]) == 1, sq.getAsInteger(data[i][7]) == 1, sq.getAsInteger(data[i][8]) == 1, SQLiteQuery.dateTimeFormat.parse(sq.getAsString(data[i][9])), sq.getAsString(data[i][10]) != null ? SQLiteQuery.dateTimeFormat.parse(sq.getAsString(data[i][10])) : null, data[i][11] == null ? null : sq.getAsLong(data[i][11])));
            }
            return tasks;
        }
        return null;
    }

    public static void delete(int id, SQLiteDatabase db, Context c) {
        //Se cancela la programación de los servicios
        GcmNetworkManager gcmManag = GcmNetworkManager.getInstance(c);
        gcmManag.cancelTask(ServiceAlarmNotification.REMIND + id, ServiceAlarmNotification.class);
        gcmManag.cancelTask(ServiceChrNotification.CHRON + id, ServiceAlarmNotification.class);
        //Se eliminan los results
        db.execSQL("DELETE FROM result WHERE activity_id=" + id);
        //Se eliminan los spans
        db.execSQL("DELETE FROM span WHERE activity_id=" + id);
        //se elimina la actividad
        db.execSQL("DELETE FROM activity WHERE id=" + id);
        BaseHelper.tryClose(db);
    }

    /*Consulta si una tarea se realizó el dia dado
    chrono es null si no tiene crono
     */
    public static Boolean getIfTaskIsDoneDay(SQLiteDatabase db, int activityId, Long chrono, Long dateTime) throws Exception {
        if (chrono == null) { //si no tiene crono
            SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*)>0 " +
                    "FROM span s " +
                    "WHERE s.activity_id=" + activityId + " AND date(s.beg_date) = date(?1)").setParam(1, new Date(dateTime));
            Object[][] data = sq.getRecords(db);
            for (int i = 0; i < data.length; i++) {
                return sq.getAsInteger(data[i][0]) == 1;
            }
        } else {  //si tiene crono
            return new Span().selectTotalTime(db, activityId, dateTime) >= chrono * 60 * 1000;
        }
        return null;
    }

    //se checkea una tarea sin crono como realizada en el día
    public static void checkTaskAsDone(int id, SQLiteDatabase db) {
        Span span = new Span();
        span.activityId = id;
        span.begDate = new Date();
        span.endDate = new Date();
        span.insert(db);
        BaseHelper.tryClose(db);
    }

    //se descheckea una tarea sin crono (como no realizada en el día)
    public static void uncheckTask(int Id, SQLiteDatabase db) {
        String sql = "DELETE FROM span WHERE activity_id=" + Id + " AND CAST((strftime('%s',beg_date)/86400) as int)=" + (int) (new Date().getTime() / 86400000);
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    //devuelve si la tarea con el id es una tarea que se debe hacer el día de hoy
    public static boolean isTodayTask(SQLiteDatabase db, int id) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT COUNT(*)>0 FROM activity ac WHERE ac.id=" + id + " AND ac." + DateUtils.getDay(new Date()));
        return sq.getInteger(db) == 1;
    }

    //da la fecha y hora de la próxima alarma de una tarea
    public static Long getNextAlarm(SQLiteDatabase db, int taskId) throws Exception {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(new Date());
        int day = DateUtils.getDayInt(cal.getTime());
        SQLiteQuery sq = new SQLiteQuery("SELECT d,l,m,x,j,v,s,reminder FROM activity WHERE id=" + taskId);
        Object[] obj = sq.getRecord(db);
        if (obj != null && obj.length != 0) {
            for (int i = 0; i < 8; i++) {
                if (sq.getAsInteger(obj[day]) == 1) {
                    //se le quita la fecha y solo se deja la hora
                    long rawDate = cal.getTimeInMillis();
                    long remindDate = DateUtils.trimDateLong(rawDate);//La fecha del reminder sin la hor
                    Date remind =  SQLiteQuery.dateTimeFormat.parse(sq.getAsString(obj[7]));
                    Calendar cal3 = new GregorianCalendar();
                    cal3.setTime(remind);
                    long remindDateTime = remindDate + (cal3.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal3.get(Calendar.MINUTE) * 60000) + (cal3.get(Calendar.SECOND) * 1000); //fecha(remindDate) y hora(c.getLong(7))
                    if ((remindDateTime >= new Date().getTime())) {
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
    @begDay es el día desde el que se cuentan los días realizados. Debe llegar con la hora en 00:00:00
    @endDay es el día hasta el que se cuentan los días realizados. Debe llegar con la hora en 00:00:00
     */
    public static ArrayList<Boolean> getDoneAndNotDoneDays(Date begDay, Date endDay, int taskId, SQLiteDatabase db) throws Exception {
        ArrayList<Boolean> doneAndNotDoneDays = new ArrayList();
        Calendar begCal = DateUtils.getGregCalendar(begDay);
        Calendar endCal = DateUtils.getGregCalendar(endDay);
        Task task = new Task().select(db, taskId);
        ArrayList<Boolean> daysOfWeek = new ArrayList(Arrays.asList(task.d, task.l, task.m, task.x, task.j, task.v, task.s));
        while (!begCal.getTime().after(endCal.getTime())) {
            if (daysOfWeek.get(DateUtils.getDayInt(begCal.getTime()))) { //si la tarea tiene que hacerse el día
                doneAndNotDoneDays.add(doneAndNotDoneDays.size(), Result.getIfTaskIsDoneDayResult(db, taskId, begCal.getTime().getTime())); //añade un true a la lista si la tarea se realizó, sino un false
            }
            begCal.add(Calendar.DAY_OF_YEAR, +1);//se incrementa la fecha del calendario en 1 día
        }
        return doneAndNotDoneDays;
    }


}

