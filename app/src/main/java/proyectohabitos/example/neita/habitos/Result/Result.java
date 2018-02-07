package proyectohabitos.example.neita.habitos.Result;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import proyectohabitos.example.neita.habitos.SQLiteQuery;
import proyectohabitos.example.neita.habitos.Task.Task;


public class Result {

    public int idResult;
    public Date day;
    public boolean done;
    public int activityId;

    public Result() {

    }

    public Result(int idResult, Date day, int activityId, boolean done) {
        this.idResult = idResult;
        this.day = day;
        this.activityId = activityId;
        this.done = done;
    }

    public ContentValues getValues() {
        ContentValues c = new ContentValues();
        c.put("day", idResult);
        c.put("activity_id", activityId);
        c.put("done", done);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("result", null, getValues());
    }

    public void update(SQLiteDatabase db, int id) {
        db.update("result", getValues(), " id=" + id + " ", null);
    }

    public void insertResultToday(SQLiteDatabase db) throws Exception {
        ArrayList<Task> task = Task.getTasks(db);
        for (int i = 0; i < task.size(); i++) {
            Result result = new Result();
            result.day = new Date();
            result.activityId = task.get(i).id;
            result.done = Task.getIfTaskIsDoneDay(db, task.get(i).id, task.get(i).chrono, new Date().getTime());
            result.insert(db);
        }
    }

    /*Consulta si una tarea se realizó el dia dado
   chrono es null si no tiene crono
   dateTime debe ser dado dandole la zona horaria correspondiente
    */
    public static Boolean getIfTaskIsDoneDayResult(SQLiteDatabase db, int activityId, Long dateTime) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT done " +
                "FROM result r " +
                "WHERE r.activity_id=" + activityId + " AND CAST((strftime('%s',r.date)/86400) as int)=" + (int) (dateTime / 86400000));//ojo
        Boolean answer = (boolean) sq.getObject(db);
        if (answer != null) {
            return sq.getAsInteger(answer) == 1;
        }
        return null;
    }
}
