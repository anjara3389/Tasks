package proyectohabitos.example.neita.habitos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.Task;

public class FrmChronometer extends AppCompatActivity {

    Timer timer;
    FloatingActionButton play, pause, stop;
    TextView txtTimer, percent;
    Span obj;
    com.mikhaellopez.circularprogressbar.CircularProgressBar pgBar;
    private Integer activityId;
    private Long currentBegTime;
    private Long id;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);
        play = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        pause = (FloatingActionButton) findViewById(R.id.frm_chrono_pause);
        stop = (FloatingActionButton) findViewById(R.id.frm_chrono_stop);
        txtTimer = (TextView) findViewById(R.id.chrono_txt_chrono);
        pgBar = (com.mikhaellopez.circularprogressbar.CircularProgressBar) findViewById(R.id.chrono_progress_bar);
        percent = (TextView) findViewById(R.id.chrono_percent);


        Bundle bundle = getIntent().getExtras();
        activityId = bundle.getInt("id");
        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
        time = new Task().select(db, activityId).chrono;

        pgBar.setProgress(0);


        final Long lastTime = new Span().selectLastTime(db, activityId) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId);
        BaseHelper.tryClose(db);


        if (lastTime != 0) {
            pgBar.setProgress((float) ((lastTime / 1000 / 60) * 100) / time);
            percent.setText(((float) ((lastTime / 1000 / 60) * 100 / time)) + "%");
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = new Timer();

                SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);

                currentBegTime = new Date().getTime();
                obj = new Span();
                obj.begDate = currentBegTime;
                obj.activityId = activityId;
                id = obj.insert(db);
                BaseHelper.tryClose(db);



                /*Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("PLAY");
                String q="SELECT id,beg_date,end_date,activity_id " +
                        "FROM span s " +
                        "WHERE s.activity_id=" + activityId+" ";

                Cursor c = db.rawQuery(q, null);

                if (c.moveToFirst())
                {
                    do {
                        System.out.println(c.getInt(0) + " " + f.format(c.getLong(1)) + " " + f.format(c.getLong(2)) + "///////");
                    } while (c.moveToNext());
                }
                else{
                    System.out.println("no hay");
                }*/

                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        FrmChronometer.this.runOnUiThread(new Runnable() {
                            public void run() {
                                long totalSec = ((new Date().getTime() - currentBegTime) + lastTime) / 1000;
                                int hours = (int) totalSec / 3600;
                                int min = (int) (totalSec % 3600) / 60;
                                int sec = (int) ((totalSec % 3600) % 60);
                                txtTimer.setText((hours < 10 ? "0" : "") + hours + ":" + (min < 10 ? "0" : "") + min + ":" + (sec < 10 ? "0" : "") + sec);

                                if ((float) ((totalSec / 60) * 100) / time <= 100) {
                                    pgBar.setProgress((float) ((totalSec / 60) * 100) / time);
                                    percent.setText(((float) ((totalSec / 60) * 100 / time)) + "%");
                                }
                            }
                        });
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, (long) 1000);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obj != null) {
                    obj.endDate = new Date().getTime();
                    SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
                    obj.update(db, id);
                    timer.cancel();

                  /*  Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println("STOP");
                    String q="SELECT id,beg_date,end_date,activity_id " +
                            "FROM span s " +
                            "WHERE s.activity_id=" + activityId+" ";

                    Cursor c = db.rawQuery(q, null);

                    if (c.moveToFirst())
                    {
                        do {
                            System.out.println(c.getInt(0) + " " + f.format(c.getLong(1)) + " " + f.format(c.getLong(2)) + "///////");
                        } while (c.moveToNext());
                    }
                    else{
                        System.out.println("no hay");
                    }*/
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Format f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("LALA");
                SQLiteDatabase db = BaseHelper.getReadable(FrmChronometer.this);
                String q = "SELECT id,beg_date,end_date,activity_id " +
                        "FROM span s " +
                        "WHERE s.activity_id=" + activityId + " ";

                Cursor c = db.rawQuery(q, null);

                if (c.moveToFirst()) {
                    do {
                        System.out.println(c.getInt(0) + " " + f.format(c.getLong(1)) + " " + f.format(c.getLong(2)) + "///////");
                    } while (c.moveToNext());
                } else {
                    System.out.println("no hay");
                }
                BaseHelper.tryClose(db);
            }
        });


    }


}

