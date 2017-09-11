package proyectohabitos.example.neita.habitos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView txtTimer;
    Span obj;
    ProgressBar pgBar;
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
        pgBar = (ProgressBar) findViewById(R.id.chrono_progress_bar);


        Bundle bundle = getIntent().getExtras();
        activityId = bundle.getInt("id");
        SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);
        time = new Task().select(db, activityId).chrono;
        pgBar.setMax((int) time);
        pgBar.setProgress(50);

        BaseHelper.tryClose(db);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = new Timer();

                SQLiteDatabase db = BaseHelper.getWritable(FrmChronometer.this);

                final Long lastTime = new Span().selectLastTime(db, activityId) == 0 ? 0 : (Long) new Span().selectLastTime(db, activityId);
                currentBegTime = new Date().getTime();

                Toast.makeText(FrmChronometer.this, lastTime + "LAST", Toast.LENGTH_SHORT).show();

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

                                pgBar.setProgress((int) (totalSec * 100 / time));
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
                    Toast.makeText(FrmChronometer.this, obj.endDate + "DATE", Toast.LENGTH_SHORT).show();
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
