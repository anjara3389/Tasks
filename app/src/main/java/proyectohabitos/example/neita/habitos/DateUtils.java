package proyectohabitos.example.neita.habitos;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    //==============================TIME ZONE====================================================

    //coje la hora de la fecha dada de Grenwitch y la convierte a la de la zona horaria correspondiente
    public static long getTimeOnCurrTimeZone(Date date) {
        return date.getTime() + TimeZone.getDefault().getOffset(date.getTime());
    }

    public static long getTimeOnCurrTimeZoneDT(Long dateTime) {
        return dateTime + TimeZone.getDefault().getOffset(dateTime);
    }

    public static Long datePlusTZ(Long dateTime) {
        return DateUtils.trimDateLong(dateTime) - TimeZone.getDefault().getOffset(DateUtils.trimDateLong(dateTime));
    }

    //============================GREGORIAN CALENDAR===============================================

    /*retorna un gregorian calendar con la fecha dada como parametro
     */
    public static GregorianCalendar getGregCalendar(Date date) {
        GregorianCalendar begCal = new GregorianCalendar();
        begCal.setTime(date);
        return begCal;
    }

    //==========================TRIM================================================================

    /*Retorna una fecha con hora 00:00:00 (sin hora) en Date
     */
    public static Date trimDate(Date date) {
        GregorianCalendar c1 = getGregCalendar(date);
        GregorianCalendar c2 = new GregorianCalendar();
        c2.set(c1.get(GregorianCalendar.YEAR), c1.get(GregorianCalendar.MONTH), c1.get(GregorianCalendar.DAY_OF_MONTH), 0, 0, 0);
        c2.set(GregorianCalendar.MILLISECOND, 0);
        return c2.getTime();
    }

    /*Retorna una fecha con hora 00:00:00 (sin hora) en Long
     */
    public static Long trimDateLong(Long dateTime) {
        return dateTime - trimTime(dateTime);
    }

    /*Retorna una hora sin fecha en Long
     */
    public static Long trimTime(Long dateTime) {
        return dateTime % (24 * 60 * 60 * 1000);
    }
    //========================GET DAYS,WEEKS,MONTHS,ETC =========================================================


    /*retorna el primer día de la semana, del mes o del año.
    *primer día de la semana: v=0
    *primer día del mes: v=1
    *primer día del año: v=2
     */
    public static Date getFirstDate(Integer v, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        if (v == 0) { //PRIMER DIA DE LA SEMANA
            cal.set(GregorianCalendar.DAY_OF_WEEK, 1);
        } else if (v == 1) {//PRIMER DIA DEL MES
            cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        } else if (v == 2) {//PRIMER DIA DEL AÑO
            cal.set(GregorianCalendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    /*retorna el ultimo día de la semana
   *primer día de la semana: v=0
   *primer día del mes: v=1
    */
    public static Date getLastDate(Integer v, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (v == 0) { //UTLIMO DIA DE LA SEMANA
            cal.set(GregorianCalendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        } else if (v == 1) {//ULTIMO DIA DEL MES
            cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return cal.getTime();
    }

    /*Retorna un arraylist con las fechas de todos los días de la semana correspondiente a la fecha dada.
     */
    public static ArrayList<Date> getDatesOfWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.getFirstDate(0, date));
        ArrayList<Date> datesCurrWeek = new ArrayList<>();
        for (int i = 0; i < 7; i++) { //llena todas las fechas de los días de la semana actual en datesCurrWeek
            datesCurrWeek.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return datesCurrWeek;
    }

    /*Retorna la respuesta de si date se encuentra dentro de la semana dateWeek
     */
    public static boolean getIfDateIntoWeek(Date dateWeek, Date date) {
        ArrayList<Date> datesWeek = getDatesOfWeek(dateWeek);
        for (int i = 0; i < datesWeek.size(); i++) {
            if (datesWeek.get(i).equals(date)) {
                return true;
            }
        }
        return false;
    }

    /*retorna la semana o el mes o el año
    * semana: var=0
    *mes: var=1
    *año: var=2
     */
    public static Long getWeekMonthOrYear(Long dateTime, int var) {
        if (var == 0) {
            return dateTime;
        } else if (var == 1) {
            return (long) DateUtils.getGregCalendar(new Date(dateTime)).get(GregorianCalendar.MONTH);
        } else {
            return (long) DateUtils.getGregCalendar(new Date(dateTime)).get(GregorianCalendar.YEAR);
        }
    }


    //=========================================OTROS========================================================================================

    /*retorna el dia de la semana correspondiente a la fecha dada como una letra.
    ejemp: si es martes retorna m
    */
    public static String getDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? "l" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? "m" :
                (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? "x" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? "j" :
                        (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? "v" : (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? "s" : "d")))));
    }

    /*retorna el dia de la semana correspondiente a la fecha dada como un numero de 0 a 6.
  ejemp: si es martes retorna 2
  */
    public static int getDayInt(Date date) {
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(date);
        return (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 0 : cal2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? 1 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? 2 : //para saber qué día es la fecha en i
                (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? 3 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? 4 :
                        (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? 5 : 6)))));
    }
}
