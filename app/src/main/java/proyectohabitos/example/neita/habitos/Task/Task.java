package proyectohabitos.example.neita.habitos.Task;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;

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
    public Integer chrono;

    public Task() {

    }

    public Task(Integer id, String name, boolean l, boolean m, boolean x, boolean j, boolean v, boolean s, boolean d, Long sinceDate, Long reminder, Integer chrono) {
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

    public void insert(SQLiteDatabase db) {
        db.insert("activity", null, getValues());
        BaseHelper.tryClose(db);
    }

    public void update(SQLiteDatabase db, Integer id) {
        db.update("activity", getValues(), " id=" + id + " ", null);
        BaseHelper.tryClose(db);
    }

    public Task select(SQLiteDatabase db, Integer id) {
        String sql = "SELECT id,name,l,m, x,j,v,s,d,since_date,reminder, chrono " +
                "FROM activity " +
                "WHERE id=" + id;

        Cursor c = db.rawQuery(sql, null);
        Task task = new Task();
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            task = new Task(id, c.getString(1), c.getInt(2) == 1, c.getInt(3) == 1, c.getInt(4) == 1, c.getInt(5) == 1, c.getInt(6) == 1, c.getInt(7) == 1, c.getInt(8) == 1, c.getLong(9), c.getLong(10), c.isNull(11) ? null : c.getInt(11));
        }
        BaseHelper.tryClose(db);
        return task;
    }

    public static void delete(int id, SQLiteDatabase db) {
        String sql = "DELETE FROM activity WHERE id=" + id;
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    public static String getDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? "l" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? "m" :
                (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? "x" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? "j" :
                        (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? "v" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? "s" : "d")))));
    }
}