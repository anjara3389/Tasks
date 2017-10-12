package proyectohabitos.example.neita.habitos.Services.AlarmNotification;

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

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.Task;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA LANZAR UNA NOTIFICACIÓN CUANDO EL TIEMPO DE LA TAREA SE ACABE.
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle (gcm)
//se necesita agregar el servicio en el manifest
//EN ESTA CLASE DE SERVICIO SE PUEDE PROGRAMAR EN CUANTO TIEMPO SE LANZA LA NOTIFICACIÓN

public class ServiceAlarmNotification extends GcmTaskService {
    private static final String NOTIFIC_ALARM = "notificAlarm";

    public ServiceAlarmNotification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle bundle = taskParams.getExtras();
        SQLiteDatabase db = BaseHelper.getReadable(this);
        //se lanza la notificación
        fireNotification(new Task().select(db, bundle.getInt("activityId")).name);
        //se inicia el sonido
        Intent in = new Intent(this, ServiceAlarmSound.class);
        startService(in);
        //se programa la siguiente alarma
        Long time = Task.getNextAlarm(db, bundle.getInt("activityId"));
        if (time != null) {
            scheduleNotificationFire(time, this, bundle.getInt("activityId"));
        }
        BaseHelper.tryClose(db);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        mGcmNetworkManager.cancelTask(NOTIFIC_ALARM, ServiceAlarmNotification.class);
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
                .setTag(NOTIFIC_ALARM)
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
