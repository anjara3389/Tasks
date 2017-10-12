package proyectohabitos.example.neita.habitos.Services.AlarmNotification;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import proyectohabitos.example.neita.habitos.R;

//PARA EJECUTAR LA ALARMA(SONIDO) DEL CRONÓMETRO
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
//se necesita agregar el servicio en el manifest
public class ServiceAlarmSound extends Service {
    private MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.alarm);
        player.setLooping(false); // Set looping
        player.setVolume(100, 100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isLooping() || player.isLooping()) {
            player.stop();
        }
        player.release();
    }

    public static void stopSound(Context ctx) {
        ctx.getApplicationContext().stopService(new Intent(ctx.getApplicationContext(), ServiceAlarmSound.class));
    }
}
