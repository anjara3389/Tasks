package proyectohabitos.example.neita.habitos.Task.FragmentsTasks;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateOnTZone;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Statistics.FrmStatistics;
import proyectohabitos.example.neita.habitos.Task.FrmTask;
import proyectohabitos.example.neita.habitos.Task.LstTask;
import proyectohabitos.example.neita.habitos.Task.Task;
import proyectohabitos.example.neita.habitos.adapters.CustomAdapterAllTasks;


public class FrgAllTasks extends Fragment implements YesNoDialogFragment.MyDialogDialogListener {
    private ListView lvTasks;
    ArrayList<LstTask> list;
    FloatingActionButton btn;
    private LstTask task;
    private int posit;
    private static final int DELETE_TASK = 1;
    private static final int CHECK_TASK = 2;
    private static final int UNCHECK_TASK = 3;

    @Override
    public void onResume() { //actualiza después de editar
        super.onResume();
        upload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_all_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frg_all_taks_lst);
        btn = (FloatingActionButton) rootView.findViewById(R.id.frg_all_tasks_btn);
        upload();

        registerForContextMenu(lvTasks);


        //cuando se seleccciona un item del list view
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posit = list.get(position).getIdTask();
                task = list.get(position);
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FrmTask.class);
                i.putExtra("isNew", true);
                startActivityForResult(i, 1);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        upload();
    }

    public void upload() {
        list = getTasks();
        CustomAdapterAllTasks adapter = new CustomAdapterAllTasks(list, getContext());
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<LstTask> getTasks() {
        ArrayList<LstTask> data = new ArrayList();
        SQLiteDatabase db = BaseHelper.getReadable(getContext());

        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        ArrayList<Date> datesCurrWeek = new ArrayList<>();

        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) { //cambia la fecha del calendario hasta que encuentra el primer día de la semana que es lunes
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        for (int i = 0; i < 7; i++) { //llena todas las fechas de los días de la semana actual en datesCurrWeek
            datesCurrWeek.add(cal.getTime());
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Cursor c = db.rawQuery("SELECT a.id, " + //0
                "a.name," +//1
                "a.reminder, " +//2
                "a.l, " + //3
                "a.m, " + //4
                "a.x, " + //5
                "a.j, " + //6
                "a.v, " + //7
                "a.s, " + //8
                "a.d, " +//9*/
                "a.chrono " +//10
                "FROM Activity a " +
                "ORDER BY a." + Task.getDay(new Date()) + " DESC", null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                Toast.makeText(getContext(), "ACTIVIDAD___" + c.getString(1), Toast.LENGTH_LONG).show();
                ArrayList<Boolean> doneDays = new ArrayList(Arrays.asList(null, null, null, null, null, null, null));

                    for (int i = 0; i < datesCurrWeek.size(); i++) { //si hay un span en una tarea y una fecha,por cada una de las tareas  y por cada una de las fechas

                        Cursor d = db.rawQuery("SELECT COUNT(*)>0 " +
                                "FROM span s " +
                                "WHERE s.activity_id=" + c.getInt(0) + " AND CAST((s.beg_date/86400000) as int)=" + (int) (datesCurrWeek.get(i).getTime() / 86400000), null);

                        if (d.moveToFirst()) {
                            Calendar cal2 = new GregorianCalendar();
                            cal2.setTime(datesCurrWeek.get(i));
                            int day = cal2.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? 0 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY ? 1 : //para saber qué día es la fecha en i
                                    (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ? 2 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY ? 3 :
                                            (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? 4 : (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ? 5 : 6)))));
                            if (d.getInt(0) == 1) {  //si hay un span en la tarea y la fecha
                                doneDays.set(day, true); //cambia el dato del arraylist en la posición correspondiente

                            }
                        }
                    }
                    for (int i = 3; i <= 9; i++) {
                        if (c.getInt(i) == 1) {
                            if (doneDays.get(i - 3) == null) {
                                doneDays.set(i - 3, false);
                            }
                        }
                    }

                if (!c.isNull(10)) {//si tiene chrono
                    for (int i = 0; i < datesCurrWeek.size(); i++) {
                        if (doneDays.get(i) != null) {
                            doneDays.set(i, new Span().selectLastTime(db, c.getInt(0), datesCurrWeek.get(i)) >= c.getInt(10) * 60 * 1000);
                        }
                    }
                }

                LstTask task = new LstTask(c.getInt(0), c.getString(1), c.getLong(2), doneDays, c.isNull(10) ? null : c.getInt(10), false);
                data.add(task);
            }

            while (c.moveToNext()); //mientras nos podamos mover hacia la sguiente
        }


        BaseHelper.tryClose(db);

        return data;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecciona una Acción");

        String msg = "";
        SQLiteDatabase db = BaseHelper.getReadable(getContext());
        Cursor c = db.rawQuery("SELECT COUNT(*)>0,(SELECT COUNT(*)>0 FROM activity ac WHERE ac.id=" + posit + " AND ac." + Task.getDay(new Date()) + ") " +
                "FROM span s " +
                "WHERE s.activity_id=" + posit, null);
        if (c.moveToFirst()) {
            if (c.getInt(1) == 1) {
                if (task.getChrono() == null) {
                    msg = c.getInt(0) == 1 ? "Desmarcar" : "Marcar";
                } else {
                    msg = "Iniciar";
                }
                menu.add(0, 0, 0, msg);
            }
        }

        menu.add(0, 1, 0, "Editar");
        menu.add(0, 2, 0, "Eliminar");
        menu.add(0, 6, 0, "Estadísticas");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0 && item.getTitle() == "Marcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Marcar", "¿Haz realizado esta actividad hoy?", CHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            return true;
        } else if (item.getItemId() == 0 && item.getTitle() == "Iniciar") {
            Intent i = new Intent(getActivity(), FrmChronometer.class);
            i.putExtra("id", posit);
            startActivityForResult(i, 1);
            upload();
            return true;
        } else if (item.getItemId() == 0 && item.getTitle() == "Desmarcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Desmarcar", "¿Desmarcar actividad?", UNCHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
            return true;
        } else if (item.getItemId() == 1) {
            Intent i = new Intent(getActivity(), FrmTask.class);
            i.putExtra("id", posit);
            i.putExtra("isNew", false);
            startActivity(i);
            return true;
        } else if (item.getItemId() == 2) {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Eliminar", "¿Desea eliminar la actividad?", DELETE_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
            return true;
        } else if (item.getItemId() == 6) {
            Intent i = new Intent(getActivity(), FrmStatistics.class);
            startActivity(i);
            return true;
        } else {
            return false;
        }

    }

    private void checkTaskAsDone(int id) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());

        String sql = "INSERT INTO span (activity_id,beg_date,end_date) VALUES (" + id + ",'" + DateOnTZone.getTimeOnCurrTimeZone() + "','" + DateOnTZone.getTimeOnCurrTimeZone() + "')";
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    private void uncheckTask(int Id) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());

        Format f = new SimpleDateFormat("yyyy-MM-dd");

        String sql = "DELETE FROM span WHERE activity_id=" + Id + " AND CAST((beg_date/86400000) as int)=" + (int) (DateOnTZone.getTimeOnCurrTimeZone() / 86400000);
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == DELETE_TASK) {
                SQLiteDatabase db = BaseHelper.getReadable(this.getContext());
                Task.delete(posit, db);
                Toast.makeText(getContext(), "Se eliminó la actividad", Toast.LENGTH_LONG).show();
                upload();
            }
            if (code == CHECK_TASK) {
                checkTaskAsDone(posit);
                Toast.makeText(getContext(), "Realizada", Toast.LENGTH_SHORT).show();
                upload();
            }
            if (code == UNCHECK_TASK) {
                uncheckTask(posit);
                Toast.makeText(getContext(), "Desmarcada", Toast.LENGTH_SHORT).show();
                upload();
            }
        }
    }
}




