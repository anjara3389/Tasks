package proyectohabitos.example.neita.habitos.Task;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.DialogFragments.NumPickersDialogFragment;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Reminder.AlarmNotification.Services.ServiceAlarmNotification;

//el formulario para crear o editar una actividad
public class FrmTask extends AppCompatActivity implements YesNoDialogFragment.MyDialogDialogListener {

    private Task obj;
    private EditText etName;

    private ImageView lun, mar, mier, juev, vier, sab, dom;
    private ImageButton reminderButton;
    private static TextView reminder, chrono, lblRemind, lblChrono, borrarPruebaFecha;
    private boolean isNew;
    private int id;
    private static long remind;
    private Long chron;
    private static ImageView imgRing, imgTemp;
    private CardView cardView;
    private static Switch switchRemind, switchChrono;
    private boolean clean;
    private ScrollView scroll;

    private static final int BACK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_task);

        //para cuando se de flecha atrás



        etName = (EditText) findViewById(R.id.activity_add_task_txt_name);

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
        lblRemind = (TextView) findViewById(R.id.frm_task_txt_alarm);
        lblChrono = (TextView) findViewById(R.id.frm_task_txt_chrono);
        cardView = (CardView) findViewById(R.id.card_view_2);
        switchRemind = (Switch) findViewById(R.id.activity_add_task_switch);
        switchChrono = (Switch) findViewById(R.id.activity_add_task_switch_2);
        imgRing = (ImageView) findViewById(R.id.activity_add_task_ring);
        imgTemp = (ImageView) findViewById(R.id.activity_add_task_image_chrono);
        scroll = (ScrollView) findViewById(R.id.frm_task_scroll);
        borrarPruebaFecha = (TextView) findViewById(R.id.prueba_fecha_inicio);


        Bundle bundle = getIntent().getExtras();
        isNew = bundle.getBoolean("isNew");

        Toolbar bar = (Toolbar) findViewById(R.id.barFrmAbout);
        setSupportActionBar(bar);
        getSupportActionBar().setTitle(isNew ? "   Nueva actividad" : "   Editar actividad");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!isNew) {
            try {
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
                borrarPruebaFecha.setVisibility(View.VISIBLE);//PRUEBA
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(FrmTask.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            borrarPruebaFecha.setVisibility(View.GONE);//PRUEBA
        }

        dom.setOnClickListener(checkUncheckDay(dom));
        lun.setOnClickListener(checkUncheckDay(lun));
        mar.setOnClickListener(checkUncheckDay(mar));
        mier.setOnClickListener(checkUncheckDay(mier));
        juev.setOnClickListener(checkUncheckDay(juev));
        vier.setOnClickListener(checkUncheckDay(vier));
        sab.setOnClickListener(checkUncheckDay(sab));


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
        lblRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRemind.setChecked(!switchRemind.isChecked());
            }
        });
        lblChrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchChrono.setChecked(!switchChrono.isChecked());
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

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    InputMethodManager imm = ((InputMethodManager) FrmTask.this.getSystemService(Context.INPUT_METHOD_SERVICE));
                    boolean isKeyboardUp = imm.isAcceptingText();

                    if (isKeyboardUp) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

    //Menu de la action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        menu.findItem(R.id.action_delete_spans).setVisible(false);
        setTitle(isNew ? "   Nueva actividad" : "   Editar actividad");
        return true;
    }

    //Menu de la action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.okTask:
                save();
                return true;
            case android.R.id.home://cuando se de flecha atrás
                if (validateBackArrowShowDialog()) {
                    YesNoDialogFragment dial = new YesNoDialogFragment();
                    dial.setInfo(this, FrmTask.this, "Se perderán los cambios", "¿Desea volver?", BACK);
                    dial.show(getSupportFragmentManager(), "MyDialog");
                } else {
                    onBackPressed();
                }
                return true;
            default:
                break;
        }
        return true;
    }

    private boolean validateBackArrowShowDialog() {
        if (isNew) {
            return isCheckedDay(lun) || isCheckedDay(mar) || isCheckedDay(mier) || isCheckedDay(juev) || isCheckedDay(vier) || isCheckedDay(sab) || isCheckedDay(dom) || !etName.getText().toString().trim().isEmpty() || switchChrono.isChecked() || switchRemind.isChecked();
        } else {
            SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
            return obj.l != isCheckedDay(lun) || obj.m != isCheckedDay(mar) || obj.x != isCheckedDay(mier) || obj.j != isCheckedDay(juev)
                    || obj.v != isCheckedDay(vier) || obj.s != isCheckedDay(sab) || obj.d != isCheckedDay(dom)
                    || (obj.reminder == null && !reminder.getText().toString().trim().isEmpty()) || (obj.reminder != null && !reminder.getText().toString().equals(f.format(obj.reminder))) ||
                    (obj.chrono == null && !chrono.getText().toString().trim().isEmpty()) || (obj.chrono != null && !chrono.getText().toString().equals(obj.chrono / 60 + " Hrs " + obj.chrono % 60 + " Min"));
        }
    }


    private View.OnClickListener checkUncheckDay(final ImageView day) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(day, null);

                //ocultar el teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
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
            if (etName.getText().toString().trim().equals("")) {
                throw new Exception("Escriba nombre");
            }
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
            obj.sinceDate = new Date();

            obj.reminder = remind != 0 ? new Date(remind) : null; //se guarda la hora del reminder con la fecha que sea
            obj.chrono = !switchChrono.isChecked() || chron == null ? null : chron;

            if (isNew) {
                id = obj.insert(db);
                BaseHelper.tryClose(db);
            } else {
                if (!borrarPruebaFecha.getText().toString().trim().isEmpty()) {//PRUEBA
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = f.parse(borrarPruebaFecha.getText().toString());
                        obj.sinceDate = date;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                obj.update(db, id);
                //Se cancela la programación de los servicios
                GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(FrmTask.this);
                mGcmNetworkManager.cancelTask(ServiceAlarmNotification.REMIND + id, ServiceAlarmNotification.class);
            }
            db = BaseHelper.getWritable(this);

            if (obj.reminder != null && Task.getNextAlarm(db, id) != null) {
                ServiceAlarmNotification.scheduleNotificationFire((int) ((Task.getNextAlarm(db, id) - DateUtils.getTimeOnCurrTimeZone(new Date())) / 1000), this, id);
            }
            BaseHelper.tryClose(db);
            Toast.makeText(this, isNew ? "Registro insertado" : "Registro actualizado", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //cuando se edita para recobrar la información de la tarea y llenarla en los campos
    private void recoverData() throws Exception {
        SQLiteDatabase db = BaseHelper.getReadable(this);
        obj = new Task().select(db, id);

        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");

        etName.setText(obj.name);
        setChecked(dom, obj.d);
        setChecked(lun, obj.l);
        setChecked(mar, obj.m);
        setChecked(mier, obj.x);
        setChecked(juev, obj.j);
        setChecked(vier, obj.v);
        setChecked(sab, obj.s);
        if (obj.reminder != null) {
            reminder.setText(f.format(new Date(obj.reminder.getTime())));
            remind = obj.reminder.getTime();
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

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == BACK) {
                onBackPressed();
            }

        }
    }

}
