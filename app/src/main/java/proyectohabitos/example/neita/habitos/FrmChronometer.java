package proyectohabitos.example.neita.habitos;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;

public class FrmChronometer extends AppCompatActivity {

    FloatingActionButton play, pause, stop;
    Chronometer chrono;

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
                chrono.start();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.stop();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.stop();
            }
        });
    }
}
