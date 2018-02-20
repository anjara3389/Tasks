package proyectohabitos.example.neita.habitos.Reminder.AlarmNotification.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.Task;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA LANZAR UNA NOTIFICACIÓN EN LA FECHA Y HORA DE LA ALARMA DE  RECORDATORIO
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle (gcm)
//se necesita agregar el servicio en el manifest
//EN ESTA CLASE DE SERVICIO SE PUEDE PROGRAMAR EN CUANTO TIEMPO SE LANZA LA NOTIFICACIÓN

public class ServiceAlarmNotification extends GcmTaskService {
    public static final String REMIND = "remind";

    public ServiceAlarmNotification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle bundle = taskParams.getExtras();
        try {
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Task task = new Task().select(db, bundle.getInt("activityId"));
            if (!Task.getIfTaskIsDoneDay(db, task.id, task.chrono, new Date())) { //si la tarea no está realizada hoy
                //se lanza la notificación
                fireNotification(task.name);
                //se inicia el sonido
                Intent in = new Intent(this, ServiceAlarmSound.class);
                startService(in);
            }
            //se programa la siguiente alarma
            Long time = Task.getNextAlarm(db, bundle.getInt("activityId"));
            if (time != null) {
                scheduleNotificationFire(time, this, bundle.getInt("activityId"));
            }
            BaseHelper.tryClose(db);
            return GcmNetworkManager.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GcmNetworkManager.RESULT_FAILURE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        // mGcmNetworkManager.cancelTask(NOTIFIC_ALARM, ServiceAlarmNotification.class);
    }

    //Lanza la notificación
    public void fireNotification(String name) {
        Intent in = new Intent(this, ServiceAlarmButtonNotific.class);
        PendingIntent pin = PendingIntent.getService(this, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Habitos")
                        .setContentText("Es tiempo de realizar: " + name)
                        .addAction(R.drawable.ok, "Aceptar", pin)
                        .setSound(null)
                        //.setContentIntent(pi)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);
        int NOTIFICATION_ID = 123456;
        NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void scheduleNotificationFire(long seconds, Context cnxt, Integer activityId) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(cnxt);
        Bundle b = new Bundle();
        b.putInt("activityId", activityId);
        OneoffTask task = new OneoffTask.Builder()
                .setService(ServiceAlarmNotification.class)
                .setTag(REMIND + activityId)
                .setExecutionWindow(seconds - 1, seconds)
                .setRequiredNetwork(NETWORK_STATE_ANY)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .setExtras(b)
                .build();
        mGcmNetworkManager.schedule(task);
    }
}
