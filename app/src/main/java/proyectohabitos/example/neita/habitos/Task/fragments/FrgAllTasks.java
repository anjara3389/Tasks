package proyectohabitos.example.neita.habitos.Task.fragments;

import android.content.Intent;
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
import java.util.Arrays;
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.Chronometer.FrmChronometer;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.SQLiteQuery;
import proyectohabitos.example.neita.habitos.Span.Span;
import proyectohabitos.example.neita.habitos.Statistics.FrmStatistics;
import proyectohabitos.example.neita.habitos.Task.FrmTask;
import proyectohabitos.example.neita.habitos.Task.LstTask;
import proyectohabitos.example.neita.habitos.Task.Task;
import proyectohabitos.example.neita.habitos.Task.adapters.AdapterAllTasks;

//Fragmento que corresponde a la pestaña de todas las tareas
public class FrgAllTasks extends Fragment implements YesNoDialogFragment.MyDialogDialogListener {
    private ListView lvTasks;
    ArrayList<LstTask> list;
    FloatingActionButton btn;
    private LstTask lstTask;
    private Integer posit;
    private static final int DELETE_TASK = 1;
    private AdapterAllTasks adapter;

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


        //cuando se seleccciona un item del list view con click sostenido
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posit = list.get(position).getIdTask();
                lstTask = list.get(position);
                return false;
            }
        });

        //cuando se seleccciona un item del list view con click
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    posit = list.get(position).getIdTask();
                    lstTask = list.get(position);
                    SQLiteDatabase db = BaseHelper.getWritable(getContext());
                    if (Task.isTodayTask(db, posit)) {
                        if (lstTask.getChrono() == null) {
                            Boolean isDoneTaskOnDay = Task.isDoneOnTheDay(db, posit, null, new Date());
                            if (isDoneTaskOnDay != null) {
                                if (isDoneTaskOnDay) {
                                    checkTask(false, db);
                                } else {
                                    checkTask(true, db);
                                }
                            }
                        } else {
                            startChrono(db);
                        }
                    } else {
                        Toast.makeText(getContext(), "La tarea no está programada para hoy", Toast.LENGTH_SHORT).show();
                    }
                    BaseHelper.tryClose(db);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        try {
            list = getAllLstTasks();
            adapter = new AdapterAllTasks(list, getContext());
            lvTasks.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*Retorna una lista de realización o no realización de cada una de las tareas en cada uno de los días de una semana (posición 0=Lunes, 1=Martes, etc) - la semana correspondiente a la fecha dada.
    Dentro del arrayList:
    si el valor de la posición dada es null, significa que la tarea no necesitaba ser realizada(el usuario no programó ese día para realizar la actividad)
    Si el val de la posición es true, significa que la tarea debía hacerse y se realizó el día indicado.
    Si el val de la pos es false, significa que debía hacerse y no se realizó el día indicado.
     */
    private ArrayList<LstTask> getAllLstTasks() throws Exception {
        ArrayList<LstTask> data = new ArrayList();
        SQLiteDatabase db = BaseHelper.getReadable(getContext());
        SQLiteQuery sq = new SQLiteQuery("SELECT a.id, " + //0
                "a.name," +//1
                "a.reminder, " +//2
                "a.d, " +//3
                "a.l," +//4
                "a.m, " + //5
                "a.x, " + //6
                "a.j, " + //7
                "a.v, " + //8
                "a.s, " + //9
                "a.chrono " +//10
                "FROM Activity a " +
                "ORDER BY a." + DateUtils.getDay(new Date()) + " DESC");
        Object[][] data2 = sq.getRecords(db);
        if (data2 != null) {
            for (int i = 0; i < data2.length; i++) {
                Date[] datesCurrWeek = DateUtils.getDatesOfWeek(new Date()); //cada una de las fechas de la semana.
                ArrayList<Boolean> doneDays = new ArrayList(Arrays.asList(null, null, null, null, null, null, null));
                //si se realizó la tarea en una fecha por cada una de las fechas de la semana
                for (int j = 0; j < datesCurrWeek.length; j++) {
                    if (sq.getAsInteger(data2[i][j + 3]) == 1) { //si hoy tenía que hacerse la actividad
                        if (Task.isDoneOnTheDay(db, sq.getAsInteger(data2[i][0]), data2[i][10] == null ? null : sq.getAsLong(data2[i][10]), datesCurrWeek[j]) != null && Task.isDoneOnTheDay(db, sq.getAsInteger(data2[i][0]), data2[i][10] == null ? null : sq.getAsLong(data2[i][10]), datesCurrWeek[j])) {
                            doneDays.set(DateUtils.getDayInt(datesCurrWeek[j]), true); //Si se realizó la tarea el día indicado se cambia el valor de ese día a true
                        } else {
                            doneDays.set(j, false);//si no re realizó la tarea el día indicado se cambia a false
                        }
                    }
                }
   
                LstTask task = new LstTask(sq.getAsInteger(data2[i][0]), sq.getAsString(data2[i][1]),sq.getAsString(data2[i][2])!=null?SQLiteQuery.dateTimeFormat.parse(sq.getAsString(data2[i][2])):null , doneDays, data2[i][10] == null ? null : sq.getAsInteger(data2[i][10]), false);
                data.add(task);
            }
        }
        BaseHelper.tryClose(db);

        return data;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        try {
            menu.setHeaderTitle("Selecciona una Acción");
            SQLiteDatabase db = BaseHelper.getReadable(getContext());
            if (Task.isTodayTask(db, posit)) {
                if (lstTask.getChrono() == null) {
                    if (Task.isDoneOnTheDay(db, posit, null, new Date())) {
                        menu.add(1, 0, 0, "Desmarcar");
                    } else {
                        menu.add(1, 1, 0, "Marcar");
                    }
                    } else {
                    menu.add(1, 2, 0, "Iniciar");
                    }
            }
            db.close();
            menu.add(1, 3, 0, "Editar");
            menu.add(1, 4, 0, "Eliminar");
            menu.add(1, 5, 0, "Estadísticas");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 1) {
            if (item.getItemId() == 0) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(getContext());
                    checkTask(false, db);
                    BaseHelper.tryClose(db);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == 1) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(getContext());
                    checkTask(true, db);
                    BaseHelper.tryClose(db);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == 2) {
                try {
                    SQLiteDatabase db = BaseHelper.getWritable(getContext());
                    startChrono(db); //cierro bd en este metodo
                    BaseHelper.tryClose(db);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == 3) {
                Intent i = new Intent(getActivity(), FrmTask.class);
                i.putExtra("id", posit);
                i.putExtra("isNew", false);
                startActivity(i);
                return true;
            } else if (item.getItemId() == 4) {
                YesNoDialogFragment dial = new YesNoDialogFragment();
                dial.setInfo(this, this.getContext(), "Eliminar", "¿Desea eliminar la actividad?", DELETE_TASK);
                dial.show(getFragmentManager(), "MyDialog");
                update();
                return true;
            } else if (item.getItemId() == 5) {
                Intent i = new Intent(getActivity(), FrmStatistics.class);
                i.putExtra("id", posit);
                startActivity(i);
                return true;
            }
        }
            return false;
    }

    private void checkTask(boolean check, SQLiteDatabase db) throws Exception {
        if (check) {
            Task.checkTaskAsDone(posit, db);
        } else {
            Task.uncheckTask(posit, db);
        }
        Toast.makeText(getContext(), check ? "Realizada" : "No Realizada", Toast.LENGTH_SHORT).show();
        update();
    }

    private void startChrono(SQLiteDatabase db) throws Exception {
        if (!Task.isDoneOnTheDay(db, posit, (long) lstTask.getChrono(), new Date())) {
            if ((Span.selectOpenedSpan(db, null) != null && Span.selectOpenedSpan(db, null).activityId == posit) || Span.selectOpenedSpan(db, null) == null) {
                Intent i = new Intent(getActivity(), FrmChronometer.class);
                i.putExtra("id", posit);
                startActivityForResult(i, 1);
                update();
            } else {
                Toast.makeText(getContext(), "Hay una tarea en curso", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "La tarea ya se realizó", Toast.LENGTH_SHORT).show();
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
                BaseHelper.tryClose(db);
            }
        }
    }
}




