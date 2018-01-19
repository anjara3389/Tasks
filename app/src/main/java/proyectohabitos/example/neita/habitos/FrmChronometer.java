package proyectohabitos.example.neita.habitos;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import proyectohabitos.example.neita.habitos.Services.ChronometerNotification.ServiceChrButtonNotific;
import proyectohabitos.example.neita.habitos.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.Services.ChronometerNotification.ServiceChrSound;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.Task;


public class FrmChronometer extends AppCompatActivity {

    private Timer timer;
    private FloatingActionButton play;
    private TextView txtTimer, percent;
    private Span obj;
    private com.mikhaellopez.circularprogressbar.CircularProgressBar pgBar;
    private Integer activityId;
    private long targetTime;
    private Long lastWholeTime = null; //el tiempo de todos los spans anteriores del día
    private long totalSec;
    private long totalSecBackwards;
    private int hours;
    private int min;
    private int sec;
    private boolean playButton;
    MyBroadcastReceiver myReceiver;
    IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);

        play = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        txtTimer = (TextView) findViewById(R.id.chrono_txt_chrono);
        pgBar = (com.mikhaellopez.circularprogressbar.CircularProgressBar) findViewById(R.id.chrono_progress_bar);
        pgBar.setProgress(0);
        percent = (TextView) findViewById(R.id.chrono_percent);

        Bundle bundle = getIntent().getExtras();
        activityId = bundle.getInt("id");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playButton == true) {//PLAY
                    if (obj != null) {

                        try {
                            play.setImageResource(R.drawable.pause);
                            playButton = false;

                            SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                            timer = new Timer();
                            lastWholeTime = new Span().selectTotalTime(db, activityId, DateUtils.getTimeOnCurrTimeZone(new Date())) == 0 ? 0 : (Long) new Span().selectTotalTime(db, activityId, DateUtils.getTimeOnCurrTimeZone(new Date()));
                            obj.begDate = Span.selectOpenedSpan(db, activityId) != null ? obj.begDate : DateUtils.getTimeOnCurrTimeZone(new Date());
                            obj.activityId = activityId;
                            obj.endDate = null;
                            obj.insert(db);
                            BaseHelper.tryClose(db);
                            timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
                            ServiceChrNotification.scheduleNotificationFire(((targetTime * 60) - (((DateUtils.getTimeOnCurrTimeZone(new Date()) - obj.begDate) + lastWholeTime) / 1000l)), FrmChronometer.this, activityId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FrmChronometer.this, e.getMessage(), Toast.LENGTH_SHORT);
                        }

                    }
                } else {//PAUSE
                    if (obj != null) {
                        play.setImageResource(R.drawable.play);
                        playButton = true;
                        obj.endDate = DateUtils.getTimeOnCurrTimeZone(new Date());
                        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                        if (activityId != null) {
                            try {
                                Span currentSpan;
                                currentSpan = Span.selectOpenedSpan(db, activityId);
                                if (currentSpan != null) {
                                    obj.update(db, currentSpan.id);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(FrmChronometer.this, e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                        if (timer != null) {
                            timer.cancel();
                        }

                        //Se cancelan los servicios
                        ServiceChrSound.stopSound(FrmChronometer.this);
                        ServiceChrButtonNotific.stopService(FrmChronometer.this);
                        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(FrmChronometer.this);
                        mGcmNetworkManager.cancelTask(ServiceChrNotification.CHRON + activityId, ServiceChrNotification.class);

                        if (totalSecBackwards == 0 || totalSecBackwards < 0) {
                            //se envía broadcast para cerrar la activity y se cierra la notificación.
                            sendBroadcast(new Intent("com.hmkcode.android.CLOSE_CRONO_ACTIVITY"));
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(FrmChronometer.this.NOTIFICATION_SERVICE);
                            mNotificationManager.cancel(12345);
                        }
                    }
                }
            }
        });

        //PARA EL BROADCAST
        myReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter("com.hmkcode.android.CLOSE_CRONO_ACTIVITY");

    }

    //Menu de la action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        menu.findItem(R.id.action_delete_spans).setVisible(false);
        menu.findItem(R.id.okTask).setVisible(false);
        setTitle("   Cronómetro");
        return true;
    }

    private void setTimer() {
        totalSec = ((DateUtils.getTimeOnCurrTimeZone(new Date()) - obj.begDate) + lastWholeTime) / 1000l;
        totalSecBackwards = (targetTime * 60) - totalSec;
        hours = (int) totalSecBackwards / 3600;
        min = (int) (totalSecBackwards % 3600) / 60;
        sec = (int) ((totalSecBackwards % 3600) % 60);

        if (totalSecBackwards >= 0) {
            txtTimer.setText((hours < 10 ? "0" : "") + hours + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec); //cambia la etiqueta del cronómetro
            if (((totalSec / 60f) * 100f / targetTime) <= 100f) { //si el tiempo no está completo
                pgBar.setProgress((int) ((totalSec / 60f) * 100f) / targetTime);
                percent.setText(((int) ((totalSec / 60f) * 100f / targetTime)) + "%");
            } else {
                pgBar.setProgress(100);
                percent.setText((100) + "%");
            }
        }
        if (totalSecBackwards <= 0) {
            play.setImageResource(R.drawable.pause); //botón y booleano del botón
            playButton = false;
            totalSecBackwards = 0;
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    public TimerTask getTimerTask() {
        return new TimerTask() {
            public void run() {
                FrmChronometer.this.runOnUiThread(new Runnable() {
                    public void run() {
                        setTimer();
                    }
                });
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() { //cuando volvemos a la aplicación o iniciamos la actividad
        super.onResume();
        try {
            //valores iniciales
            SQLiteDatabase db = BaseHelper.getReadable(FrmChronometer.this);
            targetTime = new Task().select(db, activityId).chrono; //tiempo en minutos
            obj = new Span().selectOpenedSpan(db, activityId) != null ? new Span().selectOpenedSpan(db, activityId) : new Span();
            lastWholeTime = new Span().selectTotalTime(db, activityId, DateUtils.getTimeOnCurrTimeZone(new Date())) == 0 ? 0 : (Long) new Span().selectTotalTime(db, activityId, DateUtils.getTimeOnCurrTimeZone(new Date()));
            obj.begDate = new Span().selectOpenedSpan(db, activityId) != null ? obj.begDate : DateUtils.getTimeOnCurrTimeZone(new Date());
            play.setImageResource(new Span().selectOpenedSpan(db, activityId) != null ? R.drawable.pause : R.drawable.play); //botón y booleano del botón
            playButton = new Span().selectOpenedSpan(db, activityId) == null;


            if (new Span().selectOpenedSpan(db, activityId) != null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
            }
            BaseHelper.tryClose(db);
            setTimer();

            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmChronometer.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() { //cuando dejamos la aplicación
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        unregisterReceiver(myReceiver);
    }
}






