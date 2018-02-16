package proyectohabitos.example.neita.habitos.Statistics;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.Calendar;
import java.util.Date;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.R;

public class FrgMonthStatistics extends Fragment {

    private int taskId;
    private CircularProgressBar monthBar, wholeMonthBar;
    private TextView txtPorMonth, txtWholeMonth, monthTitle;
    private Long month;

    @Override
    public void onResume() { //actualiza después de editar
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_montly_statistics, container, false);

        try {
            Bundle args = getArguments(); //traigo los parámetros
            month = (Long) args.get("month");
            taskId = (int) args.get("id");

            monthTitle = (TextView) rootView.findViewById(R.id.frg_mstat_month);
            monthBar = (CircularProgressBar) rootView.findViewById(R.id.month_pbar);
            wholeMonthBar = (CircularProgressBar) rootView.findViewById(R.id.whole_month_pbar);

            txtPorMonth = (TextView) rootView.findViewById(R.id.frm_sta_txt_per_mont);
            txtWholeMonth = (TextView) rootView.findViewById(R.id.frm_sta_txt_whole_month);

            Date d = new Date();
            d.setTime(month);
            monthTitle.setText(DateUtils.getMonth(month) + " " + DateUtils.getGregCalendar(d).get(Calendar.YEAR));

            Val v = null;
            v = Val.getStats(taskId, month, this.getContext());
            if (DateUtils.getMonth(month).equals(DateUtils.getMonth(new Date().getTime()))) { //si es el mes actual
                monthBar.setProgress(v.month);
                txtPorMonth.setText(v.month + "%");
            } else {
                monthBar.setProgress(0);
                txtPorMonth.setText("n/a");
            }

            wholeMonthBar.setProgress(v.whole);
            txtWholeMonth.setText(v.whole + "%");

            return rootView;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class Val {
        int month;
        int whole;

        public static synchronized Val getStats(int taskId, long month, Context ctx) throws Exception {
            Val rta = new Val();
            SQLiteDatabase db = BaseHelper.getReadable(ctx);
            rta.month = (int) new Statistics(taskId, false, month, db).getStatistics();
            rta.whole = (int) new Statistics(taskId, true, month, db).getStatistics();
            db.close();
            return rta;
        }
    }
}
