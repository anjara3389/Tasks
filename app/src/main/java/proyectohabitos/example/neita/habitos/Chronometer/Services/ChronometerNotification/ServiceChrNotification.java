package proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.Chronometer.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.FrmTasks;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA LANZAR UNA NOTIFICACIÓN CUANDO EL TIEMPO DEL CRONOMETRO LA TAREA SE ACABE.
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle (gcm)
//se necesita agregar el servicio en el manifest
//EN ESTA CLASE DE SERVICIO SE PUEDE PROGRAMAR EN CUANTO TIEMPO SE LANZA LA NOTIFICACIÓN

public class ServiceChrNotification extends GcmTaskService {
    public static final String CHRON = "chron";

    public ServiceChrNotification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        try {
            fireNotification();
            Intent in = new Intent(this, ServiceChrSound.class);
            startService(in);

            Bundle bundle = taskParams.getExtras();
            //se cierra el span correspondiente
            SQLiteDatabase db = BaseHelper.getReadable(this);
            Span span = new Span().selectOpenedSpan(db, bundle.getInt("activityId"));
            if (span != null) {
                span.endDate = new Date();
                span.update(db, span.id);
            }
            BaseHelper.tryClose(db);
            fireChronoActivity(bundle.getInt("activityId"));
            return GcmNetworkManager.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ServiceChrNotification.this, e.getMessage(), Toast.LENGTH_SHORT);
        }
        return GcmNetworkManager.RESULT_FAILURE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        //mGcmNetworkManager.cancelTask(NOTIFIC, ServiceChrNotification.class);
    }

    //Lanza la notificación
    public void fireNotification() {
        Intent in = new Intent(this, ServiceChrButtonNotific.class);
        PendingIntent pin = PendingIntent.getService(this, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Habitos")
                        .setContentText("Haz realizado tu actividad el día de hoy")
                        .addAction(R.drawable.ok, "Aceptar", pin)
                        .setSound(null)
                        //.setContentIntent(pi)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);
        int NOTIFICATION_ID = 12345;
        NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    //Lanza la actividad del cronometro
    public void fireChronoActivity(Integer activityId) {
        Intent i = new Intent(this, FrmChronometer.class);
        i.putExtra("id", activityId);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //--CUANDO SE CIERRA LA ACTIVIDAD EL PADRE SE ABRE (FrmTasks)--
        TaskStackBuilder b = TaskStackBuilder.create(this);
        b.addParentStack(FrmTasks.class);
        Intent addrIntent = new Intent(this, FrmTasks.class);
        addrIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        b.addNextIntent(addrIntent);
        //--------------------------------------------------------------
        b.addNextIntent(i);
        b.startActivities();

    }

    public static void scheduleNotificationFire(long seconds, Context cnxt, Integer activityId) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(cnxt);
        Bundle b = new Bundle();
        b.putInt("activityId", activityId);
        OneoffTask task = new OneoffTask.Builder()
                .setService(ServiceChrNotification.class)
                .setTag(CHRON + activityId)
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
