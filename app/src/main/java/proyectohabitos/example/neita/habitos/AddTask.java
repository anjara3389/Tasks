package proyectohabitos.example.neita.habitos;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddTask extends AppCompatActivity {

    EditText etName;
    CheckBox lun, mar, mier, juev, vier, sab, dom;
    ImageButton reminderButton;
    static TextView reminder;
    boolean isNew;
    int id;
    static long remind;
    CardView cardView;
    static Switch switchRemind;
    FloatingActionButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

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
        cardView = (CardView) findViewById(R.id.card_view_2);
        switchRemind = (Switch) findViewById(R.id.activity_add_task_switch);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        isNew = bundle.getBoolean("isNew");

        recoverData();

        if (remind != 0 && !isNew) {
            switchRemind.setChecked(true);
            reminder.setVisibility(View.VISIBLE);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().trim().equals("")) {
                    Toast.makeText(AddTask.this, "Escriba nombre", Toast.LENGTH_LONG).show();
                } else {
                    save(etName.getText().toString());
                }
            }
        });
        switchRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TimePicker mTimePicker = new TimePicker();
                    mTimePicker.show(getFragmentManager(), "Select time");
                } else {
                    remind = 0;
                    reminder.setText("");
                    reminder.setVisibility(View.GONE);
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
    }

    private void save(String name) {
        //contexto this,nombre de la base demo,no hay cursor factory y version 1
        BaseHelper helper = new BaseHelper(this, "Demo", null, null);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            //content values es un contenedor de valores
            ContentValues c = new ContentValues();
            c.put("name", name);
            c.put("l", lun.isChecked());
            c.put("m", mar.isChecked());
            c.put("x", mier.isChecked());
            c.put("j", juev.isChecked());
            c.put("v", vier.isChecked());
            c.put("s", sab.isChecked());
            c.put("d", dom.isChecked());
            c.put("since_date", new Date().getTime());
            c.put("reminder", remind);
            if (isNew) {
                db.insert("activity", null, c);
            } else {
                db.update("activity", c, " id=" + id + " ", null);
            }
            db.close();
            Toast.makeText(this, isNew ? "Registro insertado" : "Registro actualizado", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void recoverData() {
        BaseHelper helper = new BaseHelper(this, "Demo", null, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id," + //0
                "name," +//1
                "l, " + //2
                "m, " + //3
                "x, " + //4
                "j, " + //5
                "v, " + //6
                "s, " + //7
                "d, " + //8
                "reminder," +//2
                "since_date " +//3
                "FROM activity " +
                "WHERE id=" + id;
        Toast.makeText(this, sql, Toast.LENGTH_SHORT).show();

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            SimpleDateFormat f = new SimpleDateFormat("hh:mm a");

            etName.setText(c.getString(1));
            lun.setChecked(c.getInt(2) == 1);
            mar.setChecked(c.getInt(3) == 1);
            mier.setChecked(c.getInt(4) == 1);
            juev.setChecked(c.getInt(5) == 1);
            vier.setChecked(c.getInt(6) == 1);
            sab.setChecked(c.getInt(7) == 1);
            dom.setChecked(c.getInt(8) == 1);
            reminder.setText(f.format(new Date(c.getLong(9))));
            remind = c.getLong(9);
        }
        db.close();
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
