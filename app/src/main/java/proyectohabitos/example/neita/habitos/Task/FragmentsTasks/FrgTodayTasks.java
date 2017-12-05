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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Task.FrmTask;
import proyectohabitos.example.neita.habitos.Task.LstTask;
import proyectohabitos.example.neita.habitos.Task.Task;
import proyectohabitos.example.neita.habitos.adapters.CustomAdapterTodayTasks;

//fragmento pestaña tareas de hoy
public class FrgTodayTasks extends Fragment implements YesNoDialogFragment.MyDialogDialogListener {
    private ListView lvTasks;
    private ArrayList<LstTask> lstTasks;
    private FloatingActionButton btnAdd;
    private LstTask lstTask;
    private int posit;
    private static final int DELETE_TASK = 1;

    @Override
    public void onResume() { //actualiza después de editar
        super.onResume();
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_today_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frg_today_taks_lst);
        btnAdd = (FloatingActionButton) rootView.findViewById(R.id.frg_today_tasks_btn);
        update();
        registerForContextMenu(lvTasks);

        //cuando se seleccciona un item del list view con long click
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posit = lstTasks.get(position).getIdTask();
                lstTask = lstTasks.get(position);
                return false;
            }
        });
        //cuando se seleccciona un item del list view con click
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posit = lstTasks.get(position).getIdTask();
                lstTask = lstTasks.get(position);
                if (lstTask.getChrono() == null) {
                    SQLiteDatabase db = BaseHelper.getWritable(getContext());
                    if (Task.getIfTaskIsDoneDay(db, posit, null, DateUtils.getTimeOnCurrTimeZone(new Date())) != null) {
                        if (Task.getIfTaskIsDoneDay(db, posit, null, DateUtils.getTimeOnCurrTimeZone(new Date()))) {
                            checkTask(false);
                        } else {
                            checkTask(true);
                        }
                    }
                } else {
                    startChrono();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
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
        update();
    }

    public void update() {
        lstTasks = getActivities();
        CustomAdapterTodayTasks adapter = new CustomAdapterTodayTasks(lstTasks, getContext());
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<LstTask> getActivities() {
        ArrayList<LstTask> data = new ArrayList();
        SQLiteDatabase db = BaseHelper.getReadable(getContext());

        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(new Date());

        Cursor c = db.rawQuery("SELECT a.id, " + //0
                "a.name," +//1
                "a.reminder, " +//2
                "a.chrono " +//3
                "FROM Activity a " +
                "WHERE a." + DateUtils.getDay(cal.getTime()), null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                LstTask task = new LstTask(c.getInt(0), c.getString(1), c.getLong(2), null, c.isNull(3) ? null : c.getInt(3), Task.getIfTaskIsDoneDay(db, c.getInt(0), c.isNull(3) ? null : c.getLong(3), DateUtils.getTimeOnCurrTimeZone(new Date())));
                data.add(task);
            }
            while (c.moveToNext()); //mientras nos podamos mover hacia la sguiente
        }
        BaseHelper.tryClose(db);
        return data;
    }

    //Menú de cada una de las filas
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecciona una Acción");
        if (lstTask.getChrono() == null) {
            SQLiteDatabase db = BaseHelper.getReadable(getContext());
            if (Task.getIfTaskIsDoneDay(db, posit, null, DateUtils.getTimeOnCurrTimeZone(new Date())) != null) {
                if (Task.getIfTaskIsDoneDay(db, posit, null, DateUtils.getTimeOnCurrTimeZone(new Date()))) {
                    menu.add(0, 0, 0, "Desmarcar");
                } else {
                    menu.add(0, 1, 0, "Marcar");
                }
            }
        } else {
            menu.add(0, 2, 0, "Iniciar");
        }
        menu.add(0, 3, 0, "Editar");
        menu.add(0, 4, 0, "Eliminar");
    }

    //menú de cada una de las filas
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0) {
            if (item.getItemId() == 0) {
                checkTask(false);
                return true;
            } else if (item.getItemId() == 1) {
                checkTask(true);
                return true;
            } else if (item.getItemId() == 2) {
                startChrono();
                return true;
            } else if (item.getItemId() == 3) {
                Intent i = new Intent(getActivity(), FrmTask.class);
                i.putExtra("id", posit);
                i.putExtra("isNew", false);
                startActivity(i);
                return true;
            } else if (item.getItemId() == 4) {
                YesNoDialogFragment dial = new YesNoDialogFragment();
                dial.setInfo(this, this.getContext(), "Eliminar", "¿Desea eliminar la tarea?", DELETE_TASK);
                dial.show(getFragmentManager(), "MyDialog");
                update();
                return true;
            }
        }
            return false;
    }

    private void checkTask(boolean check) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());
        if (check) {
            Task.checkTaskAsDone(posit, db);
        } else {
            Task.uncheckTask(posit, db);
        }
        Toast.makeText(getContext(), check ? "Realizada" : "No Realizada", Toast.LENGTH_SHORT).show();
        update();
    }

    private void startChrono() {
        SQLiteDatabase db = BaseHelper.getReadable(getContext());
        if (!Task.getIfTaskIsDoneDay(db, posit, (long) lstTask.getChrono(), DateUtils.getTimeOnCurrTimeZone(new Date()))) {
            if ((Span.selectOpenedSpan(db, null) != null && Span.selectOpenedSpan(db, null).activityId == posit) || Span.selectOpenedSpan(db, null) == null) {
                Intent i = new Intent(getActivity(), FrmChronometer.class);
                i.putExtra("id", posit);
                startActivityForResult(i, 1);
                update();
            } else {
                Toast.makeText(getContext(), "Hay una tarea en curso.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "La tarea ya se realizó", Toast.LENGTH_SHORT).show();
        }
        BaseHelper.tryClose(db);
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == DELETE_TASK) {
                SQLiteDatabase db = BaseHelper.getReadable(this.getContext());
                Task.delete(posit, db, this.getContext());
                Toast.makeText(getContext(), "Se eliminó la Tarea", Toast.LENGTH_LONG).show();
                update();
            }
        }
    }
}




