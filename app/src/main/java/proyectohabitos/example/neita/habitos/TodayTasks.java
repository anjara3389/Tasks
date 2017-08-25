package proyectohabitos.example.neita.habitos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import proyectohabitos.example.neita.habitos.adapters.CustomAdapter;


public class TodayTasks extends Fragment implements MyDialogFragment.MyDialogDialogListener {
    public static final String ARG_OBJECT = "object";
    private ListView lvTasks;
    ArrayList<String> list;
    FloatingActionButton btn;
    private int posit;

    @Override
    public void onResume() { //actualiza después de editar
        super.onResume();
        CargarListado();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_today_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frg_today_taks_lst);
        btn = (FloatingActionButton) rootView.findViewById(R.id.frg_today_tasks_btn);
        CargarListado();
        registerForContextMenu(lvTasks);

        //cuando se seleccciona un item del list view personas
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                posit = Integer.parseInt(list.get(position).split(" ")[0]);
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddTask.class);
                i.putExtra("isNew", true);
                startActivityForResult(i, 1);
            }
        });
        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CargarListado(); //why here???? ... it uploads the list when adding??? if it works, ill explain it oki
    }
    //para después de modificar un item y volver hacia atrás para que se actualice la lista list view


    public void CargarListado() {
        list = ListaPersonas();
        CustomAdapter adapter = new CustomAdapter(list, getContext());

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<String> ListaPersonas() {
        ArrayList<String> data = new ArrayList<String>();

        BaseHelper helper = new BaseHelper(getContext(), "Demo", null, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id, " + //0
                "name," +//1
                "reminder, " +//2
                "l, " + //3
                "m, " + //4
                "x, " + //5
                "j, " + //6
                "v, " + //7
                "s, " + //8
                "d " +//9
                "FROM Activity";

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                //datos

                SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat g = new SimpleDateFormat("yyyy/MM/dd");
                String fullText;

                String text = c.getString(0) + " " + c.getString(1) + " A/"
                        + (c.getLong(2) == 0 ? "" : f.format(new Date(c.getLong(2)))) + "  ";

                ArrayList<String> str = new ArrayList<String>(
                        Arrays.asList("Lun", "Mar", "Mier", "Juev", "Vier", "Sáb", "Dom"));
                int k = 0;
                for (int i = 3; i <= 9; i++) {
                    if (c.getInt(i) == 0) {
                        str.remove(i - 3 - k);
                        k++;
                    }
                }
                for (int i = 0; i < str.size(); i++) {
                    text += str.get(i);
                    if (i != str.size() - 1) {
                        text += ", ";
                    }
                }
                data.add(text);
            }
            while (c.moveToNext()); //mientras nos podamos mover hacia la sguiente
        }
        db.close();
        return data;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecciona una Acción");
        menu.add(0, v.getId(), 0, "Iniciar");
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, "Eliminar");
    }



    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle() == "Iniciar") {
            Intent i = new Intent(getActivity(), Chronometer.class);
            startActivityForResult(i, 1);
        } else if (item.getTitle() == "Editar") {
            Intent i = new Intent(getActivity(), AddTask.class);
            i.putExtra("id", posit);
            i.putExtra("isNew", false);
            startActivity(i);
        } else if (item.getTitle() == "Eliminar") {
            MyDialogFragment dial = new MyDialogFragment();
            dial.setmContext(this.getContext());
            dial.show(getFragmentManager(), "MyDialog");
            CargarListado();
        } else {
            return false;
        }
        return true;
    }

    private void delete(int Id) {
        BaseHelper helper = new BaseHelper(getContext(), "Demo", null, null);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "DELETE FROM activity WHERE id=" + Id;
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onFinishDialog(boolean ans) {
        if (ans == true) {
            Toast.makeText(getContext(), "ELIM" + posit, Toast.LENGTH_LONG).show();
            delete(posit);
        }
    }
}




