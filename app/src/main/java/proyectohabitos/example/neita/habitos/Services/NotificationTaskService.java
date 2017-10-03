package proyectohabitos.example.neita.habitos.Services;

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
import proyectohabitos.example.neita.habitos.Span.Span;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA LANZAR UNA NOTIFICACIÓN CUANDO EL TIEMPO DE LA TAREA SE ACABE.
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle (gcm)
//se necesita agregar el servicio en el manifest
//EN ESTA CLASE DE SERVICIO SE PUEDE PROGRAMAR EN CUANTO TIEMPO SE LANZA LA NOTIFICACIÓN

public class NotificationTaskService extends GcmTaskService {
    private static final String NOTIFIC = "notific";

    public NotificationTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        fireNotification();
        Intent in = new Intent(this, AlarmTaskService.class);
        in.setAction(AlarmTaskService.PLAYSOUND);
        startService(in);

        Bundle bundle = taskParams.getExtras();
        //se cierra el span correspondiente
        SQLiteDatabase db = BaseHelper.getReadable(this);

        Span span = new Span().selectCurrentSpan(db, bundle.getInt("activityId"));

        if (span != null) {
            span.endDate = new Date().getTime();
            span.update(db, span.id);
        }

        BaseHelper.tryClose(db);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Lanza la notificación
    public void fireNotification() {
        Intent in = new Intent(this, AlarmTaskService.class);
        in.setAction(AlarmTaskService.PAUSESOUND);
        PendingIntent pin = PendingIntent.getService(this, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Habitos")
                        .setContentText("Haz realizado tu actividad el día de hoy")
                        .addAction(R.drawable.ok, "Aceptar", pin)
                        //.setContentIntent(pi)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);
        int NOTIFICATION_ID = 12345;
        NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }


    public static void scheduleNotificationFire(long seconds, Context cnxt, Integer activityId) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(cnxt);
        Bundle b = new Bundle();
        b.putInt("activityId", activityId);
        OneoffTask task = new OneoffTask.Builder()
                .setService(NotificationTaskService.class)
                .setTag(NOTIFIC)
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
