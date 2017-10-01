package proyectohabitos.example.neita.habitos;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

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
        //      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(500);
    }


    @Override
    public void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = null;
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
            onDestroy();
        }
        return super.onStartCommand(intent, i, i1);
    }
}