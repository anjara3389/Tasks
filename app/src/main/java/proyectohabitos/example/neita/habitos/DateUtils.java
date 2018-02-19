package proyectohabitos.example.neita.habitos;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static final int WEEK = 0;
    public static final int MONTH = 1;
    public static final int YEAR = 2;
    //==============================TIME ZONE====================================================

    //coje la hora de la fecha dada de Grenwitch y la convierte a la de la zona horaria correspondiente
   /* public static long getTimeOnCurrTimeZone(Date date) {
        return date.getTime() + TimeZone.getDefault().getOffset(date.getTime());
    }

    public static long getTimeOnCurrTimeZoneDT(Long dateTime) {
        return dateTime + TimeZone.getDefault().getOffset(dateTime);
    }

    //se le quita la hora y solo queda la fecha  y se le suma la diferencia horaria.
    public static Long datePlusTZ(Long dateTime) {
        return DateUtils.trimDateLong(dateTime - TimeZone.getDefault().getOffset(dateTime));
    }

    public static Date getDateOnCurrTimeZone(Date date) {
        Date date2 = new Date();
        date2.setTime(date.getTime() + TimeZone.getDefault().getOffset(date.getTime()));
        return date2;
    }
    */


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
        Date date = new Date(dateTime);
        return trimDate(date).getTime();
    }

    /*
    Retorna un mes y año sin dia, ni hora
     */
    public static Date trimMonthAndYear(Date date) {
        GregorianCalendar c1 = getGregCalendar(date);
        GregorianCalendar c2 = new GregorianCalendar();
        c2.set(c1.get(GregorianCalendar.YEAR), c1.get(GregorianCalendar.MONTH), 1, 0, 0, 0);
        c2.set(GregorianCalendar.MILLISECOND, 0);
        return c2.getTime();

    }
    //========================GET DAYS,WEEKS,MONTHS,ETC =========================================================


    /*retorna el primer día de la semana, del mes o del año.
    *primer día de la semana: v=0
    *primer día del mes: v=1
    *primer día del año: v=2
     */
    public static Date getFirstDay(int period, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        if (period == WEEK) { //PRIMER DIA DE LA SEMANA
            cal.set(GregorianCalendar.DAY_OF_WEEK, 1);
        } else if (period == MONTH) {//PRIMER DIA DEL MES
            cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        } else if (period == YEAR) {//PRIMER DIA DEL AÑO
            cal.set(GregorianCalendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    /*retorna el ultimo día de la semana
   *primer día de la semana: v=0
   *primer día del mes: v=1
    */
    public static Date getLastDate(int period, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        if (period == WEEK) { //UTLIMO DIA DE LA SEMANA
            cal.set(GregorianCalendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        } else if (period == MONTH) {//ULTIMO DIA DEL MES
            cal.set(GregorianCalendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return cal.getTime();
    }

    /*Retorna un arraylist con las fechas de todos los días de la semana correspondiente a la fecha dada.
     */
    public static Date[] getDatesOfWeek(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.getFirstDay(WEEK, date));
        Date[] datesCurrWeek = new Date[7];
        for (int i = 0; i < 7; i++) { //llena todas las fechas de los días de la semana actual en datesCurrWeek
            datesCurrWeek[i] = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return datesCurrWeek;
    }

    /*Retorna la respuesta de si date se encuentra dentro de la semana dateWeek
     */
    public static boolean isDateIntoWeek(Date dateWeek, Date date) {
        Date[] datesWeek = getDatesOfWeek(dateWeek);
        for (int i = 0; i < datesWeek.length; i++) {
            if (datesWeek[i].equals(date)) {
                return true;
            }
        }
        return false;
    }

    /*retorna la semana o el mes
     */
    public static Long getWeekOrMonth(Long dateTime, int period) {
        if (period == WEEK) {
            return dateTime;
        } else if (period == MONTH) {
            return (long) DateUtils.getGregCalendar(new Date(dateTime)).get(GregorianCalendar.MONTH);
        }
        return null;
    }

    public static ArrayList<Long> getMonthsIntoDates(Date rawBeg, Date rawEnd) {
        GregorianCalendar beg = getGregCalendar(trimMonthAndYear(rawBeg));
        GregorianCalendar end = getGregCalendar(trimMonthAndYear(rawEnd));

        ArrayList<Long> months = new ArrayList();

        while (beg.getTime().getTime() <= end.getTime().getTime()) {
            months.add(beg.getTime().getTime());
            beg.add(Calendar.MONTH, 1);
        }
        return months;
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

    /*
    Retorna el nombre del mes de la fecha dada
     */
    public static String getMonth(Long date) {
        Date d = new Date();
        d.setTime(date);
        GregorianCalendar c1 = getGregCalendar(d);
        int month = c1.get(Calendar.MONTH);
        return month == GregorianCalendar.JANUARY ? "Ene" : month == GregorianCalendar.FEBRUARY ? "Feb" :
                month == GregorianCalendar.MARCH ? "Mar" : month == GregorianCalendar.APRIL ? "Abr" :
                        month == GregorianCalendar.MAY ? "May" : month == GregorianCalendar.JUNE ? "Jun" :
                                month == GregorianCalendar.JULY ? "Jul" : month == GregorianCalendar.AUGUST ? "Ago" : month == GregorianCalendar.SEPTEMBER ? "Sept" :
                                        month == GregorianCalendar.OCTOBER ? "Oct" : month == GregorianCalendar.NOVEMBER ? "Nov" : "Dic";

    }
}
