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
    private Long currentBegTime;
    private Long id;
    private long time;

    private Long lastTime = null;

    private long totalSec;
    private int hours;
    private int min;
    private int sec;
    private boolean playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);
        play = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        txtTimer = (TextView) findViewById(R.id.chrono_txt_chrono);
        pgBar = (com.mikhaellopez.circularprogressbar.CircularProgressBar) findViewById(R.id.chrono_progress_bar);
        percent = (TextView) findViewById(R.id.chrono_percent);

        Bundle bundle = getIntent().getExtras();
        activityId = bundle.getInt("id");

        if (savedInstanceState != null && savedInstanceState.containsKey("playing")) { //
            playButton = savedInstanceState.getBoolean("playing");
        } else {
            playButton = true;
        }
        play.setImageResource(playButton == true ? R.drawable.pause : R.drawable.play);

        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
        time = new Task().select(db, activityId).chrono;
        pgBar.setProgress(0);
        getSupportActionBar().hide();

        lastTime = new Span().selectLastTime(db, activityId, new Date()) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId, new Date());
        BaseHelper.tryClose(db);

        if (lastTime != 0) {
            currentBegTime = new Date().getTime();
            setTimer();
        }
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playButton == true) {//PLAY
                    play.setImageResource(R.drawable.pause);
                    playButton = false;

                    timer = new Timer();

                    SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                    lastTime = new Span().selectLastTime(db, activityId, new Date()) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId, new Date());

                    currentBegTime = new Date().getTime();
                    obj = new Span();
                    obj.begDate = currentBegTime;
                    obj.activityId = activityId;
                    id = obj.insert(db);
                    BaseHelper.tryClose(db);

                    TimerTask timerTask = new TimerTask() {
                        public void run() {
                            FrmChronometer.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    setTimer();
                                }
                            });
                        }
                    };
                    timer.scheduleAtFixedRate(timerTask, 0, (long) 1000);
                } else {//PAUSE
                    if (obj != null) {
                        play.setImageResource(R.drawable.play);
                        playButton = true;

                        obj.endDate = new Date().getTime();
                        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                        obj.update(db, id);
                        timer.cancel();
                    }

                }
            }
        });
    }

    private void setTimer() {

        totalSec = ((new Date().getTime() - currentBegTime) + lastTime) / 1000l;
        hours = (int) totalSec / 3600;
        min = (int) (totalSec % 3600) / 60;
        sec = (int) ((totalSec % 3600) % 60);
        txtTimer.setText((hours < 10 ? "0" : "") + hours + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

        if (((totalSec / 60f) * 100f / time) <= 100f) {
            pgBar.setProgress((int) ((totalSec / 60f) * 100f) / time);
            percent.setText(((int) ((totalSec / 60f) * 100f / time)) + "%");
        } else {
            pgBar.setProgress(100);
            percent.setText((100) + "%");
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("playing", playButton);
    }
}

