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

public class CustomAdapterTodayTasks extends ArrayAdapter<LstTask> implements View.OnClickListener {

    private ArrayList<LstTask> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txt1;
        TextView txt2;
        ImageView img;
    }

    public CustomAdapterTodayTasks(ArrayList<LstTask> data, Context context) {
        super(context, R.layout.row_today_tasks, data);
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
        String dataModel2 = getItem(position).getTextChrono() + (getItem(position).getChrono() != null && getItem(position).getReminder() != null ? "   Alarma:  " : "") + getItem(position).getTextReminder();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        // final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_today_tasks, parent, false);
            viewHolder.txt1 = (TextView) convertView.findViewById(R.id.task_row_txt1);
            viewHolder.txt2 = (TextView) convertView.findViewById(R.id.task_row_txt2);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.task_row_img);

            viewHolder.img.setImageResource(getItem(position).isDone() == false ? R.drawable.check : R.drawable.chech2);

            // result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            // result=convertView;
        }


        //lastPosition = position;

        viewHolder.txt1.setText(dataModel);
        viewHolder.txt2.setText(dataModel2);
        viewHolder.img.setOnClickListener(this);
        viewHolder.img.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
