package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Task.Task;

public class Statistics {

    //Tarea a la que corresponde la estadística
    public static Task task;

    //El periodo de tiempo de las estadísticas(semanal(0)/mensual(1))
    public static int period;
    //Constantes para period
    public static final int WEEKLY = 0;
    public static final int MONTLY = 1;

    //El periodo entero o hasta el día actual
    public int until;
    //Constantes para until
    public static final int UNTIL_LAST_dAY_OF_PERIOD = 0;  //hasta el último día de la semana o mes
    public static final int UNTIL_TODAY = 1; //Hasta el día de hoy

    //Día hasta el que se cuenta las estadísticas
    public static Date untilDay;
    //lista de resultados de los días según si se realizó(true) y no se realizó(false) la tarea
    public static ArrayList<Boolean> daysResults;


    public Statistics(int taskId, int until, Long monthYear, SQLiteDatabase db) throws Exception {
        this.task = new Task().select(db, taskId);
        this.period = monthYear == null ? WEEKLY : MONTLY;
        this.until = until;
        this.untilDay = getUntilDay(period, monthYear);
        this.daysResults = Task.getDoneAndNotDoneDays(getSinceDay(), untilDay, task.id, db);
    }

    /*Da el porcentaje de realización de una tarea
    */
    public static double getStatistics() throws Exception {
        return daysResults.size() != 0 ? countDoneDays() * 100 / daysResults.size() : 0;
    }

    /*Cuenta el total de días en que se realizó la actividad
     */
    public static int countDoneDays() {
        int doneDays = 0;
        if (daysResults != null && daysResults.size() > 0) {
            for (int i = 0; i < daysResults.size(); i++) {
                if (daysResults.get(i) != null && daysResults.get(i)) {
                    doneDays++;
                }
            }
        }
        return doneDays;
    }

    /*Retorna el día hasta el que se cuenta la estadística
    */
    public Date getUntilDay(int period, Long monthYear) {
        if (period == WEEKLY) {
            return until == UNTIL_TODAY ? DateUtils.trimDate(new Date()) : until == UNTIL_LAST_dAY_OF_PERIOD ? DateUtils.trimDate(DateUtils.getLastDate(WEEKLY, new Date())) : null;
        } else if (period == MONTLY) {
            Date date = new Date();
            date.setTime(monthYear);
            return until == UNTIL_TODAY ? DateUtils.trimDate(new Date()) : until == UNTIL_LAST_dAY_OF_PERIOD ? DateUtils.trimDate(DateUtils.getLastDate(MONTLY, date)) : null;
        }
        return null;
    }

    /*Retorna el día desde el que se cuenta la estadística
    Si la  la semana o el mes en que se creó la tarea es igual a la semana o el mes seleccionado como límite(endDate),retorna el día en que se creó la tarea
    de lo contrario retorna el primer día de la semana o el primer día del mes.
     */
    private Date getSinceDay() {

        //Fecha de creación de la tarea con hora en 00:00:00
        Long creationDay = DateUtils.trimDateLong(task.sinceDate.getTime());

        //Fecha hasta la que se cuenta la estadística con hora en 00:00:00
        Long untilDayLong = untilDay.getTime();

        //Semana o mes de creación de la tarea
        Long creationPeriod = DateUtils.getWeekOrMonth(creationDay, period);

        //semana o mes hasta el que se cuenta la estadística
        Long untilPeriod = DateUtils.getWeekOrMonth(untilDayLong, period);

        //si el periodo(sem o mes) en el que se creó la tarea es igual al periodo acual
        if ((period == WEEKLY && DateUtils.isDateIntoWeek(new Date(untilDayLong), new Date(creationDay))) || (period == MONTLY && creationPeriod.equals(untilPeriod))) {

            //el día en que se creó la tarea

            Date begDay = new Date();
            begDay.setTime(creationDay);
            return begDay;
        } else {
            // primer día de la sem o del mes

            return DateUtils.getFirstDay(period, untilDay);
        }
    }
}