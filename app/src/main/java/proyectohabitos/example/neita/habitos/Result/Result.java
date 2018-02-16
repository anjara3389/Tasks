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
        c.put("day", SQLiteQuery.dateTimeFormat.format(day));
        c.put("activity_id", activityId);
        c.put("done", done);
        return c;
    }

    public int insert(SQLiteDatabase db) {
        return (int) db.insert("result", null, getValues());
    }

    public static void selectResults(SQLiteDatabase db) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT id,day,done,activity_id " +
                "FROM result r ");

        Object[][] obj = sq.getRecords(db);
        System.out.println("/////////RESULTS//////////");
        for (int i = 0; i < obj.length; i++) {
            System.out.println("id: " + obj[i][0]);
            System.out.println("day: " + obj[i][1]);
            System.out.println("done: " + obj[i][2]);
            System.out.println("activity_id: " + obj[i][3]);
        }


    }

    public static void insertResultToday(SQLiteDatabase db) throws Exception {
        ArrayList<Task> task = Task.getTasks(db);
        for (int i = 0; i < task.size(); i++) {
            Result result = new Result();
            result.day = new Date();
            result.activityId = task.get(i).id;
            System.out.println(task.get(i).chrono + "CHRONOOOOOOO");
            result.done = Task.getIfTaskIsDoneDay(db, task.get(i).id, task.get(i).chrono, new Date().getTime());
            result.insert(db);
        }
        selectResults(db);

    }

    /*Consulta si una tarea se realizó el dia dado
   chrono es null si no tiene crono
   dateTime debe ser dado dandole la zona horaria correspondiente
    */
    public static Boolean getIfTaskIsDoneDayResult(SQLiteDatabase db, int activityId, Long dateTime) throws Exception {
        SQLiteQuery sq = new SQLiteQuery("SELECT done " +
                "FROM result r " +
                "WHERE r.activity_id=" + activityId + " AND CAST((strftime('%s',r.day)/86400) as int)=" + (int) (dateTime / 86400000));//ojo
        Long ans = sq.getLong(db);
        if (ans != null) {
            return ans == 1;
        }
        return null;
    }
}
