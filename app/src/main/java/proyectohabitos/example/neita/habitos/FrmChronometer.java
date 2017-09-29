package proyectohabitos.example.neita.habitos;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);
        getSupportActionBar().hide();

        play = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        txtTimer = (TextView) findViewById(R.id.chrono_txt_chrono);
        pgBar = (com.mikhaellopez.circularprogressbar.CircularProgressBar) findViewById(R.id.chrono_progress_bar);
        pgBar.setProgress(0);
        percent = (TextView) findViewById(R.id.chrono_percent);

        Bundle bundle = getIntent().getExtras();
        activityId = bundle.getInt("id");

        //valores iniciales
        SQLiteDatabase db = BaseHelper.getReadable(FrmChronometer.this);
        targetTime = new Task().select(db, activityId).chrono; //tiempo en minutos
        obj = new Span().selectCurrentSpan(db, activityId) != null ? new Span().selectCurrentSpan(db, activityId) : new Span();
        lastWholeTime = new Span().selectLastTime(db, activityId, new Date()) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId, new Date());
        obj.begDate = new Span().selectCurrentSpan(db, activityId) != null ? obj.begDate : new Date().getTime();
        setTimer();

        play.setImageResource(new Span().selectCurrentSpan(db, activityId) != null ? R.drawable.pause : R.drawable.play); //botón y booleano del botón
        playButton = new Span().selectCurrentSpan(db, activityId) == null;

        if (new Span().selectCurrentSpan(db, activityId) != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
        }
        BaseHelper.tryClose(db);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playButton == true) {//PLAY
                    if (obj != null) {
                        play.setImageResource(R.drawable.pause);
                        playButton = false;

                        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                        timer = new Timer();
                        lastWholeTime = new Span().selectLastTime(db, activityId, new Date()) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId, new Date());
                        obj.begDate = new Span().selectCurrentSpan(db, activityId) != null ? obj.begDate : new Date().getTime();
                        obj.activityId = activityId;
                        obj.endDate = null;
                        obj.insert(db);
                        BaseHelper.tryClose(db);
                        timer.scheduleAtFixedRate(getTimerTask(), 0, (long) 1000);
                    }
                } else {//PAUSE
                    if (obj != null) {
                        play.setImageResource(R.drawable.play);
                        playButton = true;
                        obj.endDate = new Date().getTime();
                        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                        obj.update(db, new Span().selectCurrentSpan(db, activityId).id);
                        timer.cancel();
                    }
                }
            }
        });
    }

    private void setTimer() {
        totalSec = ((new Date().getTime() - obj.begDate) + lastWholeTime) / 1000l;
        totalSecBackwards = (targetTime * 60) - totalSec;
        hours = (int) totalSecBackwards / 3600;
        min = (int) (totalSecBackwards % 3600) / 60;
        sec = (int) ((totalSecBackwards % 3600) % 60);

        if (totalSecBackwards == 0) {
            play.performClick();
            new AlarmTaskService().startAlarmTask(FrmChronometer.this);
        }

        txtTimer.setText((hours < 10 ? "0" : "") + hours + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        if (((totalSec / 60f) * 100f / targetTime) <= 100f) {
            pgBar.setProgress((int) ((totalSec / 60f) * 100f) / targetTime);
            percent.setText(((int) ((totalSec / 60f) * 100f / targetTime)) + "%");
        } else {
            pgBar.setProgress(100);
            percent.setText((100) + "%");
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
}

