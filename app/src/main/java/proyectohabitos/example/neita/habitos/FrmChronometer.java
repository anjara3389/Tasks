package proyectohabitos.example.neita.habitos;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

public class FrmChronometer extends AppCompatActivity {

    FloatingActionButton play, pause, stop;
    Chronometer chrono;
    private long lastPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_chronometer);
        play = (FloatingActionButton) findViewById(R.id.frm_chrono_play);
        pause = (FloatingActionButton) findViewById(R.id.frm_chrono_pause);
        stop = (FloatingActionButton) findViewById(R.id.frm_chrono_stop);
        chrono = (Chronometer) findViewById(R.id.frm_chrono_chrono);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.setBase(lastPause != 0 ? chrono.getBase() + SystemClock.elapsedRealtime() - lastPause : SystemClock.elapsedRealtime());
                chrono.start();

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPause = SystemClock.elapsedRealtime();
                chrono.stop();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.setBase(SystemClock.elapsedRealtime());

                chrono.start();
            }
        });
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Toast.makeText(FrmChronometer.this, SystemClock.elapsedRealtime() + "", Toast.LENGTH_SHORT).show();
                if (SystemClock.elapsedRealtime() == 3000) {
                    chrono.stop();
                    Toast.makeText(FrmChronometer.this, "STOP", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
