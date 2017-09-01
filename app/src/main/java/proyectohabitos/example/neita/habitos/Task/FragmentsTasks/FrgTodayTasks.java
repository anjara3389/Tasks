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
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.FrmTask;
import proyectohabitos.example.neita.habitos.Task.LstTask;
import proyectohabitos.example.neita.habitos.adapters.CustomAdapter;


public class FrgTodayTasks extends Fragment implements YesNoDialogFragment.MyDialogDialogListener {
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
        View rootView = inflater.inflate(R.layout.frg_today_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frg_today_taks_lst);
        btn = (FloatingActionButton) rootView.findViewById(R.id.frg_today_tasks_btn);
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
        list = getActivities();
        CustomAdapter adapter = new CustomAdapter(list, getContext());
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<LstTask> getActivities() {
        ArrayList<LstTask> data = new ArrayList();
        SQLiteDatabase db = BaseHelper.getReadable(getContext());

        Cursor c = db.rawQuery("SELECT a.id, " + //0
                "a.name," +//1
                "a.reminder, " +//2
              /*PARA EL DE TODAS LAS TAREAS
                "a.l, " + //3
                "a.m, " + //4
                "a.x, " + //5
                "a.j, " + //6
                "a.v, " + //7
                "a.s, " + //8
                "a.d, " +//9*/
                "a.chrono, " +//3
                "(SELECT COUNT(*)>0 " +
                "FROM span s " +
                "WHERE s.activity_id=a.id) " +//4
                "FROM Activity a ", null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
               /* PARA EL DE TODAS LAS TAREAS
                ArrayList<String> days = new ArrayList(
                        Arrays.asList("Lun", "Mar", "Mier", "Juev", "Vier", "Sáb", "Dom"));
                int k = 0;
                for (int i = 3; i <= 9; i++) {
                    if (c.getInt(i) == 0) {
                        days.remove(i - 3 - k);
                        k++;
                    }
                }*/
                LstTask task = new LstTask(c.getInt(0), c.getString(1), c.getLong(2), null, c.isNull(3) ? null : c.getInt(3), c.getInt(4) == 1);
                data.add(task);
            }
            while (c.moveToNext()); //mientras nos podamos mover hacia la sguiente
        }
        BaseHelper.tryClose(db);
        return data;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        SQLiteDatabase db = BaseHelper.getReadable(getContext());

        Cursor c = db.rawQuery("SELECT COUNT(*)>0 " +
                "FROM span " +
                "WHERE activity_id=" + posit, null);

        menu.setHeaderTitle("Selecciona una Acción");

        String msg = "";

        if (c.moveToFirst()) {
            if (task.getChrono() == null) {
                msg = c.getInt(0) == 1 ? "Desmarcar" : "Marcar";
            } else {
                msg = "Iniciar";
            }
        }
        menu.add(0, v.getId(), 0, msg);
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, "Eliminar");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Marcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Marcar", "¿Haz realizado esta actividad hoy?", CHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
        } else if (item.getTitle() == "Iniciar") {
            Intent i = new Intent(getActivity(), FrmChronometer.class);
            startActivityForResult(i, 1);
            upload();
        } else if (item.getTitle() == "Desmarcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Desmarcar", "¿Desmarcar actividad?", UNCHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
        } else if (item.getTitle() == "Editar") {
            Intent i = new Intent(getActivity(), FrmTask.class);
            i.putExtra("id", posit);
            i.putExtra("isNew", false);
            startActivity(i);
        } else if (item.getTitle() == "Eliminar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Eliminar", "¿Desea eliminar la actividad?", DELETE_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
        } else {
            return false;
        }
        return true;
    }

    private void delete(int id) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());

        String sql = "DELETE FROM activity WHERE id=" + id;
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    private void checkTaskAsDone(int id) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());
        Format f = new SimpleDateFormat("yyyy-MM-dd");

        String sql = "INSERT INTO span (activity_id,beg_date,end_date) VALUES (" + id + ",'" + f.format(new Date()) + "','" + f.format(new Date()) + "')";
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    private void uncheckTask(int Id) {
        SQLiteDatabase db = BaseHelper.getWritable(getContext());

        String sql = "DELETE FROM span WHERE activity_id=" + Id;
        db.execSQL(sql);
        BaseHelper.tryClose(db);
    }

    @Override
    public void onFinishDialog(boolean ans, int code) {
        if (ans == true) {
            if (code == DELETE_TASK) {
                delete(posit);
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




