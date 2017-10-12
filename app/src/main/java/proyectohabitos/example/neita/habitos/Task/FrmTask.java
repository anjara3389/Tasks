package proyectohabitos.example.neita.habitos.Task;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateOnTimeZone;
import proyectohabitos.example.neita.habitos.DialogFragments.NumPickersDialogFragment;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Services.AlarmNotification.ServiceAlarmNotification;

public class FrmTask extends AppCompatActivity {

    Task obj;
    EditText etName;
    CheckBox lun, mar, mier, juev, vier, sab, dom;
    ImageButton reminderButton;
    static TextView reminder, chrono;
    boolean isNew;
    int id;
    static long remind;
    Long chron;
    static ImageView imgRing, imgTemp;
    CardView cardView;
    static Switch switchRemind, switchChrono;
    FloatingActionButton btn;
    boolean clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_task);

        etName = (EditText) findViewById(R.id.activity_add_task_txt_name);
        btn = (FloatingActionButton) findViewById(R.id.activity_add_task_add_button);
        lun = (CheckBox) findViewById(R.id.activity_add_task_chk_lun);
        mar = (CheckBox) findViewById(R.id.activity_add_task_chk_mar);
        mier = (CheckBox) findViewById(R.id.activity_add_task_chk_mierc);
        juev = (CheckBox) findViewById(R.id.activity_add_task_chk_juev);
        vier = (CheckBox) findViewById(R.id.activity_add_task_chk_vier);
        sab = (CheckBox) findViewById(R.id.activity_add_task_chk_sab);
        dom = (CheckBox) findViewById(R.id.activity_add_task_chk_dom);
        reminderButton = (ImageButton) findViewById(R.id.activity_add_task_reminder2);
        reminder = (TextView) findViewById(R.id.activity_add_task_reminder);
        chrono = (TextView) findViewById(R.id.activity_add_task_chrono);
        cardView = (CardView) findViewById(R.id.card_view_2);
        switchRemind = (Switch) findViewById(R.id.activity_add_task_switch);
        switchChrono = (Switch) findViewById(R.id.activity_add_task_switch_2);
        imgRing = (ImageView) findViewById(R.id.activity_add_task_ring);
        imgTemp = (ImageView) findViewById(R.id.activity_add_task_image_chrono);

        Bundle bundle = getIntent().getExtras();
        isNew = bundle.getBoolean("isNew");

        if (!isNew) {
            id = bundle.getInt("id");
            recoverData();
            if (remind != 0) {
                switchRemind.setChecked(true);
                imgRing.setVisibility(View.VISIBLE);
                reminder.setVisibility(View.VISIBLE);
                clean = false;
            }
            if (chron != null) {
                switchChrono.setChecked(true);
                chrono.setVisibility(View.VISIBLE);
                imgTemp.setVisibility(View.VISIBLE);
                chrono.setText(chron / 60 + " Hrs " + chron % 60 + " Min");
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().trim().equals("")) {
                    Toast.makeText(FrmTask.this, "Escriba nombre", Toast.LENGTH_LONG).show();
                } else {
                    save();
                }
            }
        });
        switchRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimePicker mTimePicker = new TimePicker();
                    mTimePicker.show(getFragmentManager(), "Recordatorio");
                } else {
                    remind = 0;
                    imgRing.setVisibility(View.GONE);
                    reminder.setText("");
                    reminder.setVisibility(View.GONE);
                }
            }
        });

        switchChrono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    NumPickersDialogFragment numPicksDialog = new NumPickersDialogFragment();
                    numPicksDialog.show(getSupportFragmentManager(), "Cronómetro");
                } else {
                    chrono.setText("");
                    chrono.setVisibility(View.GONE);
                    imgTemp.setVisibility(View.GONE);

                    if (isNew || (!isNew && clean == false)) {
                        chron = null;
                        clean = true;
                    }
                }
            }
        });

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker mTimePicker = new TimePicker();
                mTimePicker.show(getFragmentManager(), "Select time");
            }
        });
        chrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumPickersDialogFragment numPicksDialog = new NumPickersDialogFragment();
                numPicksDialog.show(getSupportFragmentManager(), "Cronómetro");
            }
        });
        getSupportActionBar().hide();
    }

    private void save() {
        try {
            if (!lun.isChecked() && !mar.isChecked() && !mier.isChecked() && !juev.isChecked() && !vier.isChecked() && !sab.isChecked() && !dom.isChecked()) {
                throw new Exception("Seleccionar por lo menos un día");
            }
            SQLiteDatabase db = BaseHelper.getWritable(this);
            if (isNew) {
                obj = new Task();
            }
            obj.name = etName.getText().toString();
            obj.l = lun.isChecked();
            obj.m = mar.isChecked();
            obj.x = mier.isChecked();
            obj.j = juev.isChecked();
            obj.v = vier.isChecked();
            obj.s = sab.isChecked();
            obj.d = dom.isChecked();
            obj.sinceDate = DateOnTimeZone.getTimeOnCurrTimeZone(new Date());
            obj.reminder = remind % (24 * 60 * 60 * 1000);
            obj.chrono = !switchChrono.isChecked() || chron == null ? null : chron;

            if (isNew) {
                int id = obj.insert(db);
                ServiceAlarmNotification.scheduleNotificationFire(Task.getNextAlarm(db, id), this, id);
                BaseHelper.tryClose(db);

            } else {
                obj.update(db, id);
            }
            Toast.makeText(this, isNew ? "Registro insertado" : "Registro actualizado", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void recoverData() {
        SQLiteDatabase db = BaseHelper.getReadable(this);
        obj = new Task().select(db, id);

        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");

        etName.setText(obj.name);
        lun.setChecked(obj.l);
        mar.setChecked(obj.m);
        mier.setChecked(obj.x);
        juev.setChecked(obj.j);
        vier.setChecked(obj.v);
        sab.setChecked(obj.s);
        dom.setChecked(obj.d);
        if (obj.reminder != null) {
            reminder.setText(f.format(new Date(obj.reminder)));
            remind = obj.reminder;
        }
        chron = obj.chrono;
        if (chron != null) {
            chrono.setText(chron / 60 + " Hrs " + chron % 60 + " Min");
        }
    }

    public void onFinishNumbersDialog(boolean ans, int hrs, int min) {
        if (ans == true) {
            chrono.setVisibility(View.VISIBLE);
            imgTemp.setVisibility(View.VISIBLE);
            chron = (long) ((hrs * 60) + min);
            chrono.setText(chron / 60 + " Hrs " + chron % 60 + " Min");
        } else {
            switchChrono.setChecked(false);
        }
    }


    public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

            GregorianCalendar gc = new GregorianCalendar();
            gc.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
            gc.set(GregorianCalendar.MINUTE, minute);
            gc.set(GregorianCalendar.SECOND, 0);
            Date d = gc.getTime();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");

            remind = d.getTime();
            reminder.setText(df.format(d));
            if (remind != 0) {
                imgRing.setVisibility(View.VISIBLE);
                reminder.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            if (remind == 0 || remind == -1) {
                switchRemind.setChecked(false);
            }
        }
    }
}
