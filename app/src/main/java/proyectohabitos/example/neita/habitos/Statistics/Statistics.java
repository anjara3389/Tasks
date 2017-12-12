package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Task.Task;

public class Statistics {


    /*Da el porcentaje de realización de una tarea en una semana
  Si wholeWeek es true significa que coje el porcentaje con respecto a toda la semana de inicio a fin
  Si wholeWeek es false significa que coje el porcentaje desde inicio de la semana hasta el día actual(el día de hoy)
   */
    public static double getWeeklyStatistics(int taskId, boolean wholeWeek, SQLiteDatabase db) {
        Task task = new Task().select(db, taskId);
        Date endDate = wholeWeek == false ? new Date() : DateUtils.getLastDate(0, new Date());
        return getStatistics(task, 0, task.sinceDate, endDate, db);
    }

    /*Da el porcentaje de realización de una tarea en un mes
  Si wholeMonth es true significa que coje el porcentaje con respecto a  el mes completo de inicio a fin
  Si wholeMonth es false significa que coje el porcentaje desde inicio del mes hasta el día actual(el día de hoy)
   */
    public static double getMontlyStatistics(int taskId, boolean wholeMonth, Long monthYear, SQLiteDatabase db) {
        Task task = new Task().select(db, taskId);
        Date d = new Date();
        d.setTime(monthYear);
        Date endDate = wholeMonth == false ? new Date() : DateUtils.getLastDate(1, d);
        System.out.println("MES////////////////");
        System.out.println("date" + d);
        System.out.println("WHOLE WEEK" + wholeMonth);
        System.out.println("RETURN" + getStatistics(task, 1, task.sinceDate, endDate, db));
        return getStatistics(task, 1, task.sinceDate, endDate, db);
    }


    /*Da el porcentaje de realización de una tarea en un intervalo de tiempo dado
   si interv es 0 semanal,interv es 1 mensual
   a dateTime se le debe dar la zona horaria correspondiente
   Si total es true significa que coje el porcentaje con respecto a toda la semana(hasta el domingo) o el mes completo(hasta el ultimo día del mes)
   Si total es false significa que coje el porcentaje hasta el día actual(el día de hoy)
    */
    public static double getStatistics(Task task, int interv, Long rawCreationDate, Date rawEndDate, SQLiteDatabase db) {
        ArrayList<Boolean> doneAndNotDone;
        Date rawBegDay;
        int doneTasks = 0;
        rawBegDay = getbegDay(interv, rawCreationDate, rawEndDate);
        doneAndNotDone = Task.getDoneAndNotDone(rawBegDay, rawEndDate, task.id, db);
        for (int i = 0; i < doneAndNotDone.size(); i++) {//Se saca el total de trues
            if (doneAndNotDone.get(i)) {
                doneTasks++;
            }
        }
        System.out.println("size done and not done" + doneAndNotDone.size());
        return doneAndNotDone.size() != 0 ? doneTasks * 100 / doneAndNotDone.size() : 0;
    }

    //Retorna el día en que se creó la tarea si la semana o el mes en que se creó la tarea es igual a la semana o el mes seleccionado como límite(end)
    //de lo contrario retorna el primer día de la semana o el primer día del mes.
    private static Date getbegDay(int interv, Long rawCreationDate, Date rawEnd) {
        Long creationDay = DateUtils.datePlusTZ(rawCreationDate);//DÍA EN QUE SE CREÓ LA TAREA. se le quita la hora y solo queda la fecha  y se le suma la diferencia horaria.
        Long endDay = DateUtils.datePlusTZ(rawEnd.getTime());//DÍA HASTA. se le quita la hora y solo queda la fecha y se le suma la diferencia horaria.

        Long creationWeekOrMonth = DateUtils.getWeekMonthOrYear(creationDay, interv);  //SEMANA(interv 0) o MES(interv 1) de CREACIÓN DE LA TAREA
        Long now = DateUtils.getWeekMonthOrYear(endDay, interv);  //SEMANA(interv 0) o MES(interv 1) HASTA
        Date begDay;

        if (interv != 0 ? creationWeekOrMonth.equals(now) : DateUtils.getIfDateIntoWeek(new Date(endDay), new Date(creationDay))) { //si la sem o mes en el que se creó la tarea == a la sem o mes actual
            begDay = new Date();
            begDay.setTime(creationDay);//el día en que se creó la tarea
        } else {
            begDay = DateUtils.getFirstDate(interv, rawEnd); // primer día de la sem(interv == 0) o primer día del mes(interv == 1)
        }
        return begDay;
    }
}