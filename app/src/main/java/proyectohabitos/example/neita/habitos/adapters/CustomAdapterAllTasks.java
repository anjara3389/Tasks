package proyectohabitos.example.neita.habitos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.LstTask;

public class CustomAdapterAllTasks extends ArrayAdapter<LstTask> implements View.OnClickListener {

    private ArrayList<LstTask> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txt1;
        ImageView lun;
        ImageView mar;
        ImageView mier;
        ImageView juev;
        ImageView viern;
        ImageView sab;
        ImageView dom;
    }

    public CustomAdapterAllTasks(ArrayList<LstTask> data, Context context) {
        super(context, R.layout.row_all_tasks, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        //don't delete this, we'll need it later okiiiii
/*
            int position=(Integer) v.getTag();
            Object object= getItem(position);
            DataModel dataModel=(DataModel)object;

            switch (v.getId())
            {
                case R.id.item_info:
                    Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }*/
    }

    //private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String dataModel = getItem(position).getName();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        // final View result;
      /*  if(getItem(position).getName().equals("Prueba")) {
            for (int i = 0; i < 7; i++) {
                Toast.makeText(getContext(), "Actividad:" + getItem(position).getName() + " Día:" + getItem(position).getDays().get(i), Toast.LENGTH_SHORT).show();
            }
        }*/


   //     if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_all_tasks, parent, false);
            viewHolder.txt1 = (TextView) convertView.findViewById(R.id.all_task_row_txt1);
            viewHolder.lun = (ImageView) convertView.findViewById(R.id.row_lun);
            viewHolder.mar = (ImageView) convertView.findViewById(R.id.row_mar);
            viewHolder.mier = (ImageView) convertView.findViewById(R.id.row_mierc);
            viewHolder.juev = (ImageView) convertView.findViewById(R.id.row_juev);
            viewHolder.viern = (ImageView) convertView.findViewById(R.id.row_viern);
            viewHolder.sab = (ImageView) convertView.findViewById(R.id.row_sab);
            viewHolder.dom = (ImageView) convertView.findViewById(R.id.row_dom);


            viewHolder.lun.setImageResource(getItem(position).getDays().get(0) == null ? R.drawable.no_filled : getItem(position).getDays().get(0) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.mar.setImageResource(getItem(position).getDays().get(1) == null ? R.drawable.no_filled : getItem(position).getDays().get(1) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.mier.setImageResource(getItem(position).getDays().get(2) == null ? R.drawable.no_filled : getItem(position).getDays().get(2) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.juev.setImageResource(getItem(position).getDays().get(3) == null ? R.drawable.no_filled : getItem(position).getDays().get(3) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.viern.setImageResource(getItem(position).getDays().get(4) == null ? R.drawable.no_filled : getItem(position).getDays().get(4) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.sab.setImageResource(getItem(position).getDays().get(5) == null ? R.drawable.no_filled : getItem(position).getDays().get(5) == false ? R.drawable.no_filled_green : R.drawable.filled);
            viewHolder.dom.setImageResource(getItem(position).getDays().get(6) == null ? R.drawable.no_filled : getItem(position).getDays().get(6) == false ? R.drawable.no_filled_green : R.drawable.filled);
/*
            // result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            // result=convertView;
        }*/


        //lastPosition = position;

        viewHolder.txt1.setText(dataModel);
        viewHolder.lun.setOnClickListener(this);
        viewHolder.lun.setTag(position);
        viewHolder.mar.setOnClickListener(this);
        viewHolder.mar.setTag(position);
        viewHolder.mier.setOnClickListener(this);
        viewHolder.mier.setTag(position);
        viewHolder.juev.setOnClickListener(this);
        viewHolder.juev.setTag(position);
        viewHolder.viern.setOnClickListener(this);
        viewHolder.viern.setTag(position);
        viewHolder.sab.setOnClickListener(this);
        viewHolder.sab.setTag(position);
        viewHolder.dom.setOnClickListener(this);
        viewHolder.dom.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
