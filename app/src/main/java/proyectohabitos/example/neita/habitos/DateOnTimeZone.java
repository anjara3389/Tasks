package proyectohabitos.example.neita.habitos;


import java.util.Date;
import java.util.TimeZone;

public class DateOnTimeZone {
    //DATE ON TIME ZONE

    //coje la hora de la fecha dada de Grenwitch y la convierte a la de la zona horaria correspondiente
    public static long getTimeOnCurrTimeZone(Date date) {
        return date.getTime() + TimeZone.getDefault().getOffset(date.getTime());
    }
}
