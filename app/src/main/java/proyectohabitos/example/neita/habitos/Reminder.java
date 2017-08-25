package proyectohabitos.example.neita.habitos;

import android.content.Intent;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Reminder extends AppCompatActivity {

    FloatingActionButton btn;
    TimePicker time;
    Boolean isNew;
    Long remind;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        btn = (FloatingActionButton) findViewById(R.id.activity_reminder_btn);
        time = (TimePicker) findViewById(R.id.activity_reminder_date_picker);

        Bundle bundle = getIntent().getExtras();
        isNew = bundle.getBoolean("isNew");

        if(!isNew){
            remind = bundle.getLong("remind");
            SimpleDateFormat f = new SimpleDateFormat("HH");
            SimpleDateFormat g = new SimpleDateFormat("mm");
            int hour=Integer.parseInt(f.format(new Date(remind)));
            int minute=Integer.parseInt(g.format(new Date(remind)));
            time.setHour(hour);
            time.setMinute(minute);
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hours = time.getCurrentHour(); //here is deprecated but i cant change to the new way it says it requires api 23 but i dont know what it means
                int minute = time.getCurrentMinute();//and here

                GregorianCalendar gc = new GregorianCalendar(); //oh gregorian calendar again ejejeje it will be your bf jajajaja
                gc.set(GregorianCalendar.HOUR_OF_DAY, hours);
                gc.set(GregorianCalendar.MINUTE, minute);
                gc.set(GregorianCalendar.SECOND, 0);
                Date d = gc.getTime();
                SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

                String label = df.format(d); //help //that would do the label how to comment it all here?

                Intent intent = getIntent();
                intent.putExtra("text", label);
                intent.putExtra("date", d.getTime());//is this not working?it has a null, so weird
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
