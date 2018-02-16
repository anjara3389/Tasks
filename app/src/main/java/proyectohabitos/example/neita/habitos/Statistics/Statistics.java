package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Task.Task;

public class Statistics {

    public static Task task;
    public static int weekMont; //0 Semanal //1 mensual
    public boolean whole; //**true= la semana(hasta el domingo)/mes entero(hasta el ultimo día del mes) ***false=hasta el día de hoy
    public static Date rawBegDay; //día en que se inicia a contar las estadísticas sin sumarle diferencia horaria
    public static Date rawEndDay; //día hasta el que se cuenta las estadísticas sin sumarle diferencia horaria
    public static ArrayList<Boolean> daysResults; //lista de boolean con los resultados de los días según si se realizó(true) y no se realizó(false) la tarea


    public Statistics(int taskId, boolean whole, Long monthYear, SQLiteDatabase db) throws Exception {
        this.task = new Task().select(db, taskId);
        this.weekMont = monthYear == null ? 0 : 1;
        this.whole = whole;

        if (weekMont == 0) { //Estadística Semanal
            rawEndDay = whole == false ? new Date() : DateUtils.getLastDate(0, new Date());
        } else if (weekMont == 1) {//Estadística Mensual
            Date d = new Date();
            d.setTime(monthYear);
            rawEndDay = whole == false ? new Date() : DateUtils.getLastDate(1, d);
        }
        rawBegDay = getBegDay();
        daysResults = Task.getDoneAndNotDoneDays(rawBegDay, rawEndDay, task.id, db);
    }

    /*Da el porcentaje de realización de una tarea en un intervalo de tiempo dado
   si interv es 0 semanal,interv es 1 mensual
   a dateTime se le debe dar la zona horaria correspondiente
   Si total es true significa que coje el porcentaje con respecto a toda la semana(hasta el domingo) o el mes completo(hasta el ultimo día del mes)
   Si total es false significa que coje el porcentaje hasta el día actual(el día de hoy)
    */
    public static double getStatistics() throws Exception {
        return daysResults.size() != 0 ? countDoneDays() * 100 / daysResults.size() : 0;
    }

    //Cuenta el total de días en que se realizó la actividad
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

    //Si la  la semana o el mes en que se creó la tarea es igual a la semana o el mes seleccionado como límite(endDate),retorna el día en que se creó la tarea
    //de lo contrario retorna el primer día de la semana o el primer día del mes.
    private static Date getBegDay() {
        Long creationDay = DateUtils.datePlusTZ(task.sinceDate.getTime());//DÍA EN QUE SE CREÓ LA TAREA. se le quita la hora y solo queda la fecha  y se le suma la diferencia horaria.
        Long endDay = DateUtils.datePlusTZ(rawEndDay.getTime());//DÍA HASTA. se le quita la hora y solo queda la fecha y se le suma la diferencia horaria.

        Long creationWeekOrMonth = DateUtils.getWeekMonthOrYear(creationDay, weekMont);  //SEMANA(interv 0) o MES(interv 1) de CREACIÓN DE LA TAREA
        Long now = DateUtils.getWeekMonthOrYear(endDay, weekMont);  //SEMANA(interv 0) o MES(interv 1) HASTA

        if ((weekMont == 0 && DateUtils.getIfDateIntoWeek(new Date(endDay), new Date(creationDay)))//Si es semanal && si la sem en el que se creó la tarea == a la sem actual
                || (weekMont == 1 && creationWeekOrMonth.equals(now))) { //Si es mensual && si el mes en el que se creó la tarea == al mes actual
            Date begDay = new Date();
            begDay.setTime(creationDay);//el día en que se creó la tarea
            return begDay;
        } else {
            return DateUtils.getFirstDate(weekMont, rawEndDay); // primer día de la sem(interv == 0) o primer día del mes(interv == 1)
        }
    }
}