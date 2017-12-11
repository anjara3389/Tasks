package proyectohabitos.example.neita.habitos.Statistics;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

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
        Bundle args = getArguments(); //traigo los parámetros
        month = (Long) args.get("month");
        taskId = (int) args.get("id");

        monthTitle = (TextView) rootView.findViewById(R.id.frg_mstat_month);
        monthBar = (CircularProgressBar) rootView.findViewById(R.id.month_pbar);
        wholeMonthBar = (CircularProgressBar) rootView.findViewById(R.id.whole_month_pbar);

        txtPorMonth = (TextView) rootView.findViewById(R.id.frm_sta_txt_per_mont);
        txtWholeMonth = (TextView) rootView.findViewById(R.id.frm_sta_txt_whole_month);
        monthTitle.setText(DateUtils.getMonth(month));

        SQLiteDatabase db = BaseHelper.getReadable(this.getContext());

        monthBar.setProgress((int) Statistics.getMontlyStatistics(taskId, false, month, db));
        txtPorMonth.setText((int) Statistics.getMontlyStatistics(taskId, false, month, db) + "%");

        wholeMonthBar.setProgress((int) Statistics.getMontlyStatistics(taskId, true, month, db));
        txtWholeMonth.setText((int) Statistics.getMontlyStatistics(taskId, true, month, db) + "%");

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
