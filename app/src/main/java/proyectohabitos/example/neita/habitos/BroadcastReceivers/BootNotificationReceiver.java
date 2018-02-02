package proyectohabitos.example.neita.habitos.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Services.AlarmNotification.ServiceAlarmNotification;
import proyectohabitos.example.neita.habitos.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.Task;

/**
 * Broadcast que sirve para cuando se reinicia el dispositivo, para volver a reiniciar las notificaciones y alarmas
 * Un BroadcastReceiver es un componente Android que permite el registro de eventos del sistema.
 * Todos los Receivers registrados para un evento serán notificados por Android una vez que éstos ocurran.
 * Por ejemplo, Android permite que aplicaciones puedan registrarse al ACTION_BOOT_COMPLETED que es un evento que lanza el sistema una vez que ha completado el proceso de arranque.
 */

public class BootNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Toast.makeText(context, "LLEGÓ!!!!!!", Toast.LENGTH_LONG).show();
            System.out.println("lalalalallaa///");
            SQLiteDatabase db = BaseHelper.getWritable(context);
            ArrayList<Task> tasks = Task.getTasks(db);
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if (task.reminder != null && Task.getNextAlarm(db, task.id) != null) { //alza la notificación y alarma del reminder
                    ServiceAlarmNotification.scheduleNotificationFire((int) ((Task.getNextAlarm(db, task.id) - DateUtils.getTimeOnCurrTimeZone(new Date())) / 1000), context, task.id);
                }
                if (task.chrono != null) {
                    Span openedSpan = new Span().selectOpenedSpan(db, task.id) != null ? new Span().selectOpenedSpan(db, task.id) : null;
                    Long lastWholeTime = new Span().selectTotalTime(db, task.id, DateUtils.getTimeOnCurrTimeZone(new Date())) == 0 ? 0 : (Long) new Span().selectTotalTime(db, task.id, DateUtils.getTimeOnCurrTimeZone(new Date()));
                    if (openedSpan != null) {
                        ServiceChrNotification.scheduleNotificationFire(((task.chrono * 60) - (((DateUtils.getTimeOnCurrTimeZone(new Date()) - openedSpan.begDate.getTime()) + lastWholeTime) / 1000l)), context, task.id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}