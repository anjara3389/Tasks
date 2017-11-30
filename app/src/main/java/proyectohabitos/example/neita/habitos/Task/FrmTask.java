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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.DialogFragments.NumPickersDialogFragment;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Services.AlarmNotification.ServiceAlarmNotification;

//el formulario para crear o editar una actividad
public class FrmTask extends AppCompatActivity {

    private Task obj;
    private EditText etName;

    private ImageView lun, mar, mier, juev, vier, sab, dom;
    private ImageButton reminderButton;
    private static TextView reminder, chrono;
    private boolean isNew;
    private int id;
    private static long remind;
    private Long chron;
    private static ImageView imgRing, imgTemp;
    private CardView cardView;
    private static Switch switchRemind, switchChrono;
    private FloatingActionButton btnOk;
    private boolean clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_task);

        etName = (EditText) findViewById(R.id.activity_add_task_txt_name);
        btnOk = (FloatingActionButton) findViewById(R.id.activity_add_task_add_button);

        lun = (ImageView) findViewById(R.id.row_lun_task);
        mar = (ImageView) findViewById(R.id.row_mar_task);
        mier = (ImageView) findViewById(R.id.row_mierc_task);
        juev = (ImageView) findViewById(R.id.row_juev_task);
        vier = (ImageView) findViewById(R.id.row_viern_task);
        sab = (ImageView) findViewById(R.id.row_sab_task);
        dom = (ImageView) findViewById(R.id.row_dom_task);

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

        lun.setOnClickListener(checkUncheckDay(lun));
        mar.setOnClickListener(checkUncheckDay(mar));
        mier.setOnClickListener(checkUncheckDay(mier));
        juev.setOnClickListener(checkUncheckDay(juev));
        vier.setOnClickListener(checkUncheckDay(vier));
        sab.setOnClickListener(checkUncheckDay(sab));
        dom.setOnClickListener(checkUncheckDay(dom));

        btnOk.setOnClickListener(new View.OnClickListener() {
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

    private View.OnClickListener checkUncheckDay(final ImageView day) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(day, null);
            }
        };
    }

    private void setChecked(final ImageView day, Boolean check) {
        if (check == null) {
            day.setImageResource(isCheckedDay(day) ? R.drawable.no_filled : R.drawable.filled);
            day.setTag(isCheckedDay(day) ? "no_filled" : "filled");
        } else {
            day.setImageResource(check ? R.drawable.filled : R.drawable.no_filled);
            day.setTag(check ? "filled" : "no_filled");
        }
    }

    private boolean isCheckedDay(ImageView day) {
        return day.getTag().equals("no_filled") ? false : true;
    }

    //guarda el registro editandolo o eliminandolo
    private void save() {
        try {
            if (!isCheckedDay(lun) && !isCheckedDay(mar) && !isCheckedDay(mier) && !isCheckedDay(juev) && !isCheckedDay(vier) && !isCheckedDay(sab) && !isCheckedDay(dom)) {
                throw new Exception("Seleccionar por lo menos un día");
            }
            SQLiteDatabase db = BaseHelper.getWritable(this);
            if (isNew) {
                obj = new Task();
            }
            obj.name = etName.getText().toString();
            obj.l = isCheckedDay(lun);
            obj.m = isCheckedDay(mar);
            obj.x = isCheckedDay(mier);
            obj.j = isCheckedDay(juev);
            obj.v = isCheckedDay(vier);
            obj.s = isCheckedDay(sab);
            obj.d = isCheckedDay(dom);
            //se le quita la hora y solo queda la fecha y dentro de la zona horaria correspondiente
            obj.sinceDate = new Date().getTime();
            obj.reminder = remind != 0 ? DateUtils.trimTime(remind) : null; //se le quita la fecha y solo se deja la hora
            obj.chrono = !switchChrono.isChecked() || chron == null ? null : chron;

            if (isNew) {
                id = obj.insert(db);
                BaseHelper.tryClose(db);
            } else {
                obj.update(db, id);
                //Se cancela la programación de los servicios
                GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(FrmTask.this);
                mGcmNetworkManager.cancelTask(ServiceAlarmNotification.REMIND + id, ServiceAlarmNotification.class);
            }
            db = BaseHelper.getWritable(this);
            if (obj.reminder != null && Task.getNextAlarm(db, id) != null) {
                Toast.makeText(this, "sec:" + (int) ((Task.getNextAlarm(db, id) - DateUtils.getTimeOnCurrTimeZone(new Date())) / 1000), Toast.LENGTH_SHORT).show();
                ServiceAlarmNotification.scheduleNotificationFire((int) ((Task.getNextAlarm(db, id) - DateUtils.getTimeOnCurrTimeZone(new Date())) / 1000), this, id);
            }
            BaseHelper.tryClose(db);
            Toast.makeText(this, isNew ? "Registro insertado" : "Registro actualizado", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //cuando se edita para recobrar la información de la tarea y llenarla en los campos
    private void recoverData() {
        SQLiteDatabase db = BaseHelper.getReadable(this);
        obj = new Task().select(db, id);

        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");

        etName.setText(obj.name);
        setChecked(lun, obj.l);
        setChecked(mar, obj.m);
        setChecked(mier, obj.x);
        setChecked(juev, obj.j);
        setChecked(vier, obj.v);
        setChecked(sab, obj.s);
        setChecked(dom, obj.d);
        if (obj.reminder != null) {
            reminder.setText(f.format(new Date(obj.reminder)));
            remind = obj.reminder;
        }
        chron = obj.chrono;
        if (chron != null) {
            chrono.setText(chron / 60 + " Hrs " + chron % 60 + " Min");
        }
    }

    //cuando se acaba de selecciona el tiempo del cronómetro
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


    //METODOS DEL DATE PICKER
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
