package proyectohabitos.example.neita.habitos;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import proyectohabitos.example.neita.habitos.adapters.CustomAdapter;
import proyectohabitos.example.neita.habitos.adapters.CustomAdapterAll;

public class AllTheTasks extends Fragment {
    private ListView lvTasks;
    private ArrayList<String> list;
    private FloatingActionButton btn;
    private FloatingActionButton prueba;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CargarListado();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.frm_all_tasks, container, false);

        lvTasks = (ListView) rootView.findViewById(R.id.frm_all_tasks_lv_tasks);
        btn = (FloatingActionButton) rootView.findViewById(R.id.frm_all_tasks_btn);
        prueba = (FloatingActionButton) rootView.findViewById(R.id.prueba);

        CargarListado();
        registerForContextMenu(lvTasks);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddTask.class);
                startActivityForResult(i, 1);
            }
        });

        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Statistics.class);
                startActivityForResult(i, 1);
            }
        });
        return rootView;
    }



    public void CargarListado() {
        list = ListaPersonas();
        CustomAdapterAll adapter = new CustomAdapterAll(list, getContext());
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        lvTasks.setAdapter(adapter);
    }

    private ArrayList<String> ListaPersonas() {
        ArrayList<String> datos = new ArrayList<String>();

        BaseHelper helper = new BaseHelper(getContext(), "Demo", null, null);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT id," + //0
                "name " +//1
                "FROM Activity";

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) //si nos podemos mover al primer elemento entonces significa que hay datos
        {
            do {
                //datos
                String linea = c.getInt(0) + " "
                        + c.getString(1) + " ";
                datos.add(linea);
            }
            while (c.moveToNext()); //mientras nos podamos mover hacia la sguiente
        }
        db.close();
        return datos;

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecciona una Acción");
        menu.add(0, v.getId(), 0, "Iniciar");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, "Eliminar");
    }
    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()=="Iniciar"){
            Intent i=new Intent(getActivity(), Chronometer.class);
            startActivityForResult(i,1);
        }
        else if(item.getTitle()=="Editar"){
            Toast.makeText(getContext(),"Editar",Toast.LENGTH_LONG).show();
        }
        else if(item.getTitle()=="Eliminar"){
            Toast.makeText(getContext(),"Eliminar",Toast.LENGTH_LONG).show();
        }else{
            return false;
        }
        return true;
    }



}
