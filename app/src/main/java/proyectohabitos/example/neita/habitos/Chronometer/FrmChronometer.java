package proyectohabitos.example.neita.habitos.Chronometer;

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

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.Chronometer.BroadcastReceivers.CloseChronoBcastReceiver;
import proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification.ServiceChrButtonNotific;
import proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification.ServiceChrSound;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.Task;


public class FrmChronometer extends AppCompatActivity {

    private FloatingActionButton btnPlayPause;
    private TextView txtTimer, txtPercent;
    private com.mikhaellopez.circularprogressbar.CircularProgressBar pgBar;

    /*span abierto actual(que tiene begDate pero no endDate)*/
    private Span span;

    /*tarea a la que corresponde el cronómetro*/
    private Task task;

    /*tiempo total de todos los spans anteriores del día sumados*/
    private Long totalTime = null;

    /*tiempo faltante para completar el cronómetro*/
    private long missingSeconds;

    /*temporizador*/
    private Timer timer;

    /*dice si el botón de play/pause está en play o pause*/
    private int btnPlayStatus;

    /*constantes para el btnPlayStatus*/
    private static final int PAUSE = 0;
    private static final int PLAY = 1;

    /*broadcast para cerrar la actividad del cronómetro*/
    private CloseChronoBcastReceiver closeChronoBCastReceiver;

    /*intent filter para el broadcast*/
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);

        btnPlayPause = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        txtTimer = (TextView) findViewById(R.id.chrono_txt_chrono);
        pgBar = (com.mikhaellopez.circularprogressbar.CircularProgressBar) findViewById(R.id.chrono_progress_bar);
        pgBar.setProgress(0);
        txtPercent = (TextView) findViewById(R.id.chrono_percent);
        try {
            SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
            Bundle bundle = getIntent().getExtras();
            task = new Task().select(db, bundle.getInt("id"));
            BaseHelper.tryClose(db);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FrmChronometer.this, e.getMessage(), Toast.LENGTH_SHORT);
        }


        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnPlayStatus == PLAY) {//PLAY
                    if (span != null) {

                        try {
                            btnPlayPause.setImageResource(R.drawable.pause);
                            btnPlayStatus = PAUSE;

                            SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                            timer = new Timer();
                            totalTime = new Span().selectTotalTime(db, task.id, new Date().getTime()) == 0 ? 0 : (Long) new Span().selectTotalTime(db, task.id, new Date().getTime());
                            span.begDate = Span.selectOpenedSpan(db, task.id) != null ? span.begDate : new Date();//ojo
                            span.activityId = task.id;
                            span.endDate = null;
                            span.insert(db);
                            BaseHelper.tryClose(db);
                            timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
                            ServiceChrNotification.scheduleNotificationFire(((task.chrono * 60) - (((new Date().getTime() - span.begDate.getTime()) + totalTime) / 1000l)), FrmChronometer.this, task.id);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FrmChronometer.this, e.getMessage(), Toast.LENGTH_SHORT);
                        }

                    }
                } else {//PAUSE
                    if (span != null) {
                        btnPlayPause.setImageResource(R.drawable.play);
                        btnPlayStatus = PLAY;
                        span.endDate = new Date();
                        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                        if (task.id != null) {
                            try {
                                Span currentSpan;
                                currentSpan = Span.selectOpenedSpan(db, task.id);
                                if (currentSpan != null) {
                                    span.update(db, currentSpan.id);
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
                        mGcmNetworkManager.cancelTask(ServiceChrNotification.CHRON + task.id, ServiceChrNotification.class);

                        if (missingSeconds == 0 || missingSeconds < 0) {
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
        closeChronoBCastReceiver = new CloseChronoBcastReceiver();
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
        long totalSec = ((new Date().getTime() - span.begDate.getTime()) + totalTime) / 1000l;
        missingSeconds = (task.chrono * 60) - totalSec;

        int hours = (int) missingSeconds / 3600;
        int min = (int) (missingSeconds % 3600) / 60;
        int sec = (int) ((missingSeconds % 3600) % 60);

        if (missingSeconds >= 0) {
            txtTimer.setText((hours < 10 ? "0" : "") + hours + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec); //cambia la etiqueta del cronómetro
            if (((totalSec / 60f) * 100f / task.chrono) <= 100f) { //si el tiempo no está completo
                pgBar.setProgress((int) ((totalSec / 60f) * 100f) / task.chrono);
                txtPercent.setText(((int) ((totalSec / 60f) * 100f / task.chrono)) + "%");
            } else {
                pgBar.setProgress(100);
                txtPercent.setText((100) + "%");
            }
        }
        if (missingSeconds <= 0) {
            btnPlayPause.setImageResource(R.drawable.pause); //botón y booleano del botón
            btnPlayStatus = PAUSE;
            missingSeconds = 0;
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
            span = new Span().selectOpenedSpan(db, task.id) != null ? new Span().selectOpenedSpan(db, task.id) : new Span();
            totalTime = new Span().selectTotalTime(db, task.id, new Date().getTime()) == 0 ? 0 : (Long) new Span().selectTotalTime(db, task.id, new Date().getTime());
            span.begDate = new Span().selectOpenedSpan(db, task.id) != null ? span.begDate : new Date();
            btnPlayPause.setImageResource(new Span().selectOpenedSpan(db, task.id) != null ? R.drawable.pause : R.drawable.play); //botón y booleano del botón
            btnPlayStatus = new Span().selectOpenedSpan(db, task.id) == null ? 1 : 0;


            if (new Span().selectOpenedSpan(db, task.id) != null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
            }
            BaseHelper.tryClose(db);
            setTimer();

            registerReceiver(closeChronoBCastReceiver, intentFilter);
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
        unregisterReceiver(closeChronoBCastReceiver);
    }
}






