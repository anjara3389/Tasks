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
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DialogFragments.YesNoDialogFragment;
import proyectohabitos.example.neita.habitos.FrmChronometer;
import proyectohabitos.example.neita.habitos.R;
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

      /*  Calendar cal = new GregorianCalendar();
        Date date = new Date();
        cal.setTime(date);
        ArrayList<String> dates = new ArrayList<>();
        Format f = new SimpleDateFormat("yyyy-MM-dd");

        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        for (int i = 0; i < 7; i++) {
            dates.add(f.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }*/
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
                "FROM Activity a ", null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                ArrayList<Boolean> doneDays = new ArrayList(Arrays.asList(null, null, null, null, null, null, null));

              /*for (int i = 0; i < dates.size(); i++) {
                   String s="SELECT COUNT(*)>0 " +//0
                           "FROM span s " +
                           "WHERE s.activity_id=" + c.getInt(0) + " AND s.beg_date='" + dates.get(i) + "'";
                    Cursor d = db.rawQuery(s, null);

                   if(d.moveToFirst())
                    {
                        do {
                          for (int j = 0; j <7; j++) {
                                if (d.getInt(0) == 1) {
                                    doneDays.set(j,true);
                                }
                            }
                        }
                        while(d.moveToNext());
                    }
             }*/
                for (int i = 3; i <= 9; i++) {
                    if (c.getInt(i) == 1) {
                        doneDays.set(i - 3, false);

                    }
                }
                for (int i = 0; i < doneDays.size(); i++) {
                    // Toast.makeText(getContext(),"Actividad:"+c.getString(1)+ " Día:"+  doneDays.get(i), Toast.LENGTH_SHORT).show();
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
        if (task.getChrono() == null) {
            SQLiteDatabase db = BaseHelper.getReadable(getContext());
            Cursor c = db.rawQuery("SELECT COUNT(*)>0 FROM span WHERE activity_id=" + posit, null);
            if (c.moveToFirst()) {
                msg = c.getInt(0) == 1 ? "Desmarcar" : "Marcar";
            }
        } else {
            msg = "Iniciar";
        }

        menu.add(0, 0, 0, msg);
        menu.add(0, 1, 0, "Editar");
        menu.add(0, 2, 0, "Eliminar");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0 && item.getTitle() == "Marcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Marcar", "¿Haz realizado esta actividad hoy?", CHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            return true;
        } else if (item.getItemId() == 0 && item.getTitle() == "Iniciar") {
            Intent i = new Intent(getActivity(), FrmChronometer.class);
            startActivityForResult(i, 1);
            upload();
            return true;
        } else if (item.getItemId() == 0 && item.getTitle() == "Desmarcar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Desmarcar", "¿Desmarcar actividad?", UNCHECK_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
            return true;
        } else if (item.getItemId() == 1 && item.getTitle() == "Editar") {
            Intent i = new Intent(getActivity(), FrmTask.class);
            i.putExtra("id", posit);
            i.putExtra("isNew", false);
            startActivity(i);
            return true;
        } else if (item.getItemId() == 2 && item.getTitle() == "Eliminar") {
            YesNoDialogFragment dial = new YesNoDialogFragment();
            dial.setInfo(this, this.getContext(), "Eliminar", "¿Desea eliminar la actividad?", DELETE_TASK);
            dial.show(getFragmentManager(), "MyDialog");
            upload();
            return true;
        } else {
            return false;
        }

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




