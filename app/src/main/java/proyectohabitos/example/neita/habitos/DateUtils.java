package proyectohabitos.example.neita.habitos;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    //DATE ON TIME ZONE

    //coje la hora de la fecha dada de Grenwitch y la convierte a la de la zona horaria correspondiente
    public static long getTimeOnCurrTimeZone(Date date) {
        return date.getTime() + TimeZone.getDefault().getOffset(date.getTime());
    }

    public static long getTimeOnCurrTimeZoneDT(Long dateTime) {
        return dateTime + TimeZone.getDefault().getOffset(dateTime);
    }

    /*retorna el primer día de la semana, del mes o del año.
    *primer día de la semana: v=0
    *primer día del mes: v=1
    *primer día del año: v=2
     */
    public static Date getFirstDate(Integer v, Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        if (v == 0) { //PRIMER DIA DE LA SEMANA
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) { //cambia la fecha del calendario(restando días) hasta que encuentra el primer día de la semana que es lunes
                cal.add(Calendar.DAY_OF_YEAR, -1);
            }
        } else if (v == 1) {//PRIMER DIA DEL MES
            cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        } else if (v == 2) {//PRIMER DIA DEL AÑO
            cal.set(GregorianCalendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    /*retorna un gregorian calendar con la fecha dada como parametro
     */
    public static GregorianCalendar getGregCalendar(Date date) {
        GregorianCalendar begCal = new GregorianCalendar();
        begCal.setTime(date);
        return begCal;
    }
    
    public static Date trimDate(Date date) {
        GregorianCalendar c1 = getGregCalendar(date);
        GregorianCalendar c2 = new GregorianCalendar();
        c2.set(c1.get(GregorianCalendar.YEAR), c1.get(GregorianCalendar.MONTH), c1.get(GregorianCalendar.DAY_OF_MONTH), 0, 0, 0);
        c2.set(GregorianCalendar.MILLISECOND, 0);
        return c2.getTime();
    }

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
  ejemp: si es martes retorna 1
  */
    public static int getDayInt(Date date) {
        Calendar cal2 = new GregorianCalendar();
        cal2.setTime(date);
        return cal2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? 0 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? 1 : //para saber qué día es la fecha en i
                (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? 2 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? 3 :
                        (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? 4 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? 5 : 6)))));
    }
}
