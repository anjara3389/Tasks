package proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

//SERVICIO QUE FUNCIONA ENTRE EL SERVICIO DE LA NOTIFICACIÓN DEL CRONOMETRO Y EL DE LA ALARMA CUANDO SE PRESIONA EL BOTÓN DE LA NOTIFICACIÓN
//se necesita agregar el servicio en el manifest
public class ServiceChrButtonNotific extends Service {
    public ServiceChrButtonNotific() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sendBroadcast(new Intent("com.hmkcode.android.CLOSE_CRONO_ACTIVITY"));
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(12345);
        ServiceChrSound.stopSound(this);
        return START_STICKY;

    }

    public static void stopService(Context ctx) {
        ctx.getApplicationContext().stopService(new Intent(ctx.getApplicationContext(), ServiceChrSound.class));
    }

}
