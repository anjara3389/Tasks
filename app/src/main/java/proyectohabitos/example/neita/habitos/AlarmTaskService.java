package proyectohabitos.example.neita.habitos;

import android.media.MediaPlayer;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

//PARA EJECUTAR LA ALARMA DEL CRONÓMETRO
//PERMITE PROGRAMAR UNA TAREA AUNQUE LA PANTALLA DEL CELULAR ESTÉ APAGADA
// se necesita incluir librería en el gradle
//se necesita agregar el servicio en el manifest

public class AlarmTaskService extends GcmTaskService {


    // private static final String TAG = MessengerService.class.getCanonicalName();

    //private Messenger messenger;
    //  private ServiceScheduler serviceScheduler;
    private MediaPlayer mediaPlayer = null;

    public AlarmTaskService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        mediaPlayer = MediaPlayer.create(this, R.raw.bells);
        mediaPlayer.start();

        return 0;
    }

    @Override
    public void onDestroy() {
        mediaPlayer = null;
        //serviceScheduler = null;
        super.onDestroy();
    }
}
