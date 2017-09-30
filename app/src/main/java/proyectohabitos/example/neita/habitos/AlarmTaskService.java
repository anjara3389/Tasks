package proyectohabitos.example.neita.habitos;


import android.media.MediaPlayer;
import android.os.Bundle;

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
        //      Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        v.vibrate(500);
        return GcmNetworkManager.RESULT_SUCCESS;
        //}
    }

    @Override
    public void onDestroy() {
        mediaPlayer = null;
        super.onDestroy();
    }

    public void startAlarmTask() {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this.getApplicationContext());
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
}
