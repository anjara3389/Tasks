package proyectohabitos.example.neita.habitos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

//PARA EJECUTAR LA ALARMA DEL CRONÓMETRO
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle
//se necesita agregar el servicio en el manifest

public class AlarmTaskService extends GcmTaskService {
    private static final String ALARM = "alarm";
    private static MediaPlayer mediaPlayer = null;
    public static final String ACTION1 = "ACTION1";

    public AlarmTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        //  if (taskParams.equals(ALARM)) {
        mediaPlayer = MediaPlayer.create(this, R.raw.bells);
        mediaPlayer.setLooping(true);

        mediaPlayer.start();
        CustomNotification();
        //      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(500);
        return GcmNetworkManager.RESULT_SUCCESS;
        //}
        // return GcmNetworkManager.RESULT_FAILURE;
    }

    @Override
    public void onDestroy() {
        mediaPlayer = null;
        super.onDestroy();
    }

    public void startAlarmTask(Context ctx) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(ctx);
        Bundle b = new Bundle();
        OneoffTask task = new OneoffTask.Builder()
                .setService(AlarmTaskService.class)
                .setTag(ALARM)
                .setExecutionWindow(0, 1)
                .setRequiredNetwork(NETWORK_STATE_ANY)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .setExtras(b)
                .build();
        mGcmNetworkManager.schedule(task);
    }

    public void CustomNotification() {

        // Intent i=new Intent(this,FrmChronometer.class);
        Intent in = new Intent(this, AlarmTaskService.class);
        in.setAction(AlarmTaskService.ACTION1);
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

    // Receives the command to begin doing work, for which it spawns another thread.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION1.equals(action)) {
                if (mediaPlayer.isPlaying() == true) {
                    mediaPlayer.stop();
                    mediaPlayer = null;
                }
            }
        }
        return Service.START_STICKY;
    }



}
