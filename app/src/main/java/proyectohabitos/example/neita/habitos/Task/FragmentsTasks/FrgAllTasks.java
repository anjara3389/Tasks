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
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateOnTimeZone;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Statistics.FrmStatistics;
import proyectohabitos.example.neita.habitos.Task.FrmTask;
import proyectohabitos.example.neita.habitos.Task.LstTask;
import proyectohabitos.example.neita.habitos.Task.Task;
import proyectohabitos.example.neita.habitos.adapters.CustomAdapterAllTasks;

//Fragmento pestaña todas las tareas
public class FrgAllTasks extends Fragment implements YesNoDialogFragment.MyDialogDialogListener {
    private ListView lvTasks;
    ArrayList<LstTask> list;
    FloatingActionButton btn;
    private LstTask task;
    private Integer posit;
    private static final int DELETE_TASK = 1;
    private static final int CHECK_TASK = 2;
    private static final int UNCHECK_TASK = 3;
    private CustomAdapterAllTasks adapter;

    @Override
    public void onResume() { //actualiza después de editar
        super.onResume();
        update();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_all_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frg_all_taks_lst);
        btn = (FloatingActionButton) rootView.findViewById(R.id.frg_all_tasks_btn);
        update();

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
        update();
    }

    public void update() {
        list = getTasks();
        adapter = new CustomAdapterAllTasks(list, getContext());
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<LstTask> getTasks() {
        ArrayList<LstTask> data = new ArrayList();
        SQLiteDatabase db = BaseHelper.getReadable(getContext());

        Cursor c = db.rawQuery("SELECT a.id, " + //0
                "a.name," +//1
                "a.reminder, " +//2
                "a.chrono " +//3
                "FROM Activity a " +
                "ORDER BY a." + Task.getDay(new Date()) + " DESC", null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                ArrayList<Boolean> doneDays = Task.getDoneDaysOfTheWeekByActivity(db, c.getInt(0), c.isNull(3) ? null : c.getLong(3), new Date());
                LstTask task = new LstTask(c.getInt(0), c.getString(1), c.getLong(2), doneDays, c.isNull(3) ? null : c.getInt(3), false);
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
        SQLiteDatabase db = BaseHelper.getReadable(getContext());
        Cursor c = db.rawQuery("SELECT COUNT(*)>0 FROM activity ac WHERE ac.id=" + posit + " AND ac." + Task.getDay(new Date()), null);
        if (c.moveToFirst()) {
            if (c.getInt(0) == 1) {
                if (task.getChrono() == null) {
                    menu.add(0, 0, 0, Task.getIfTaskIsDoneDay(db, posit, null, DateOnTimeZone.getTimeOnCurrTimeZone(new Date())) ? "Desmarcar" : "Marcar");
                } else {
                    if (!Task.getIfTaskIsDoneDay(db, posit, (long) task.getChrono(), DateOnTimeZone.getTimeOnCurrTimeZone(new Date()))) {
                        menu.add(0, 0, 0, "Iniciar");
                    }
                }
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
            SQLiteDatabase db = BaseHelper.getReadable(getContext());
            if ((Span.selectOpenedSpan(db, null) != null && Span.selectOpenedSpan(db, null).activityId == posit) || Span.selectOpenedSpan(db, null) == null) {
                Intent i = new Intent(getActivity(), FrmChronometer.class);
                i.putExtra("id", posit);
                startActivityForResult(i, 1);
                update();
            } else {
                Toast.makeText(getContext(), "Hay una tarea en curso", Toast.LENGTH_SHORT).show();
            }
            BaseHelper.tryClose(db);
            return true;
        } else if (item.getItemId() == 0 && item.getTitle() == "Desmarcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Desmarcar", "¿Desmarcar actividad?", UNCHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            update();
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
            update();
            return true;
        } else if (item.getItemId() == 6) {
            Intent i = new Intent(getActivity(), FrmStatistics.class);
            startActivity(i);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == DELETE_TASK) {
                SQLiteDatabase db = BaseHelper.getReadable(this.getContext());
                Task.delete(posit, db, this.getContext());
                Toast.makeText(getContext(), "Se eliminó la actividad", Toast.LENGTH_LONG).show();
                update();
            }
            if (code == CHECK_TASK) {
                SQLiteDatabase db = BaseHelper.getWritable(getContext());
                Task.checkTaskAsDone(posit, db);
                Toast.makeText(getContext(), "Realizada", Toast.LENGTH_SHORT).show();
                update();
            }
            if (code == UNCHECK_TASK) {
                SQLiteDatabase db = BaseHelper.getWritable(getContext());
                Task.uncheckTask(posit, db);
                Toast.makeText(getContext(), "Desmarcada", Toast.LENGTH_SHORT).show();
                update();
            }
        }
    }
}




