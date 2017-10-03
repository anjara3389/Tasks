package proyectohabitos.example.neita.habitos.Services;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import proyectohabitos.example.neita.habitos.R;

//PARA EJECUTAR LA ALARMA DEL CRONÓMETRO
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
//se necesita agregar el servicio en el manifest
public class AlarmTaskService extends Service {
    private static MediaPlayer mediaPlayer = null;
    public static final String PLAYSOUND = "playSound";
    public static final String PAUSESOUND = "pauseSound";

    public AlarmTaskService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        if (intent.getAction().equals(PLAYSOUND)) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bells);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        if (intent.getAction().equals(PAUSESOUND)) {
            sendBroadcast(new Intent("com.hmkcode.android.CLOSE_CRONO_ACTIVITY"));
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(12345);
            onDestroy();
        }
        return super.onStartCommand(intent, i, i1);
    }
}
