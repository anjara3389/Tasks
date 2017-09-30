package proyectohabitos.example.neita.habitos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA LANZAR UNA NOTIFICACIÓN CUANDO EL TIEMPO DE LA TAREA SE ACABE.
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle
//se necesita agregar el servicio en el manifest

public class NotificationTaskService extends GcmTaskService {
    public static final String ACTION1 = "ACTION1";
    private static final String NOTIFIC = "notific";

    private Context ctx;
    private long seconds;

    public NotificationTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        releaseNotification();
        new AlarmTaskService().startAlarmTask();
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new AlarmTaskService().onDestroy();
    }

    public void releaseNotification() {
        // Intent i=new Intent(this,FrmChronometer.class);
        Intent in = new Intent(this, NotificationTaskService.class);
        in.setAction(NotificationTaskService.ACTION1);
        //  PendingIntent pi= PendingIntent.getActivity(this, 0, i, 0);
        PendingIntent pin = PendingIntent.getService(this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("Habitos")
                        .setContentText("Haz realizado tu actividad el día de hoy")
                        .addAction(R.drawable.ok, "Aceptar", pin)
                        .setPriority(Notification.PRIORITY_MAX).setDefaults(Notification.DEFAULT_ALL);
        //.setContentIntent(pin)
        int NOTIFICATION_ID = 12345;
        NotificationManager nManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void startNotificationTask(long seconds) {
        this.seconds = seconds;
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this.getApplicationContext());
        Bundle b = new Bundle();
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
