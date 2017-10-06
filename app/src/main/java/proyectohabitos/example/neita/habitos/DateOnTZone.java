package proyectohabitos.example.neita.habitos;


import java.util.Date;
import java.util.TimeZone;

public class DateOnTZone {
    //DATE ON TIME ZONE

    //coje la hora actual en Grenwitch y la convierte a la de la zona horaria correspondiente
    public static long getTimeOnCurrTimeZone() {
        return new Date().getTime() + TimeZone.getDefault().getOffset(new Date().getTime());
    }
}
